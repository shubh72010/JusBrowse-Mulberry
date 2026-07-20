"use strict";

// ═══════════════════════════════════════════════════════
// JusBrowse Content Script — document_start
//
// Architecture: Site Context + UI/Privacy Utility Layer.
// Fingerprinting control is fully delegated to GeckoView RFP 
// to ensure stable async behavior and prevent cross-realm JS errors.
// ═══════════════════════════════════════════════════════

(function () {
    // Guard: prevent re-execution if content script fires multiple times in same frame.
    if (window.__jusbrowse_ran) return;
    window.__jusbrowse_ran = true;

    // ── Global Session Seed ──
    const globalSeed = (function () {
        const buf = new Uint32Array(2);
        crypto.getRandomValues(buf);
        return (buf[0] ^ (buf[1] << 16)).toString(16);
    })();

    // ── Simple Hash Function ──
    function hash(str) {
        let hash = 5381;
        for (let i = 0; i < str.length; i++) {
            hash = ((hash << 5) + hash) + str.charCodeAt(i);
        }
        return (hash >>> 0).toString(16);
    }

    // ════════════════════════════════════════════════
    // 1. SITE IDENTITY LAYER
    //    Provides a stable, isolated context per site for internal use.
    // ════════════════════════════════════════════════
    function getSiteContext(host) {
        return {
            seed: hash(globalSeed + host),
            site: host,
            stable: true
        };
    }

    // Initialize context for current site
    const host = window.location.hostname;
    const siteContext = getSiteContext(host);
    // The siteContext is for internal extension logic (e.g., partitioning, feature flags)
    // and is deliberately NOT exported to the page realm to prevent probing.

    // ════════════════════════════════════════════════
    // 12. MEDIA EXTRACTION (Airlock)
    // ════════════════════════════════════════════════
    browser.runtime.onMessage.addListener((message, sender, sendResponse) => {
        if (message.type === 'extractMedia') {
            console.log('[JusBrowse] Airlock: Extraction request received.');
            const runExtraction = () => {
                try {
                    const media = { images: [], videos: [], audio: [] };

                    document.querySelectorAll('img').forEach(function (img) {
                        if (img.src && !img.src.startsWith('data:') && img.src.startsWith('http')) {
                            media.images.push({
                                url: img.src,
                                title: img.alt || img.title || '',
                                metadata: img.naturalWidth + 'x' + img.naturalHeight
                            });
                        }
                    });
                    document.querySelectorAll('*').forEach(function (el) {
                        try {
                            const bg = window.getComputedStyle(el).backgroundImage;
                            if (bg && bg !== 'none' && bg.includes('url(')) {
                                const urlMatch = bg.match(/url\(["']?(.+?)["']?\)/);
                                if (urlMatch && urlMatch[1] && !urlMatch[1].startsWith('data:') && urlMatch[1].startsWith('http')) {
                                    media.images.push({ url: urlMatch[1], title: 'Background Image', metadata: '' });
                                }
                            }
                        } catch (e) { }
                    });
                    document.querySelectorAll('video').forEach(function (video) {
                        if (video.src && video.src.startsWith('http')) {
                            media.videos.push({ url: video.src, title: video.title || video.getAttribute('aria-label') || '', metadata: video.duration ? Math.floor(video.duration) + 's' : '' });
                        }
                    });
                    document.querySelectorAll('audio').forEach(function (audio) {
                        if (audio.src && audio.src.startsWith('http')) {
                            media.audio.push({ url: audio.src, title: audio.title || audio.getAttribute('aria-label') || '', metadata: audio.duration ? Math.floor(audio.duration) + 's' : '' });
                        }
                    });
                    sendResponse(media);
                } catch (err) { sendResponse({ images: [], videos: [], audio: [] }); }
            };
            if (document.readyState === 'complete') runExtraction();
            else window.addEventListener('load', runExtraction, { once: true });
            return true;
    } else if (message.type === 'toggle_boomer') {
        if (message.enabled) {
            if (window.__boomerModeEnabled) return;
            window.__boomerModeEnabled = true;

            // Add hover style
            var style = document.getElementById('__boomer_hover_style');
            if (!style) {
                style = document.createElement('style');
                style.id = '__boomer_hover_style';
                style.textContent = '.__boomer_hover { outline: 4px solid red !important; outline-offset: -3px; background-color: rgba(255,0,0,0.15) !important; transition: outline 0.1s ease; }';
                (document.head || document.documentElement).appendChild(style);
            }

            if (!window.__boomerTouchStart) {
                window.__boomerTouchStart = function (e) {
                    if (!window.__boomerModeEnabled) return;
                    var prev = document.querySelector('.__boomer_hover');
                    if (prev) prev.classList.remove('__boomer_hover');
                    var el = e.target;
                    // Walk up to a meaningful element (skip text nodes, document, etc.)
                    while (el && el.nodeType !== 1) el = el.parentNode;
                    if (el && el !== document.documentElement && el !== document.body) {
                        el.classList.add('__boomer_hover');
                        window.__boomerTarget = el;
                    }
                };
                window.__boomerTouchEnd = function (e) {
                    if (!window.__boomerModeEnabled) return;
                    e.preventDefault(); e.stopPropagation();
                    var el = window.__boomerTarget || e.target;
                    if (el && el.nodeType === 1 && el !== document.documentElement && el !== document.body) {
                        el.classList.remove('__boomer_hover');
                        try { el.remove(); } catch (_) {
                            // Fallback: remove via parent
                            try { if (el.parentNode) el.parentNode.removeChild(el); } catch (_) {}
                        }
                    }
                    window.__boomerTarget = null;
                };
                document.addEventListener('touchstart', window.__boomerTouchStart, { passive: true, capture: true });
                document.addEventListener('touchend', window.__boomerTouchEnd, { passive: false, capture: true });
            }
        } else {
            if (!window.__boomerModeEnabled) return;
            window.__boomerModeEnabled = false;
            var styleToRemove = document.getElementById('__boomer_hover_style');
            if (styleToRemove) styleToRemove.remove();
            var hoveredEls = document.querySelectorAll('.__boomer_hover');
            hoveredEls.forEach(function (el) { el.classList.remove('__boomer_hover'); });
            if (window.__boomerTouchStart) {
                document.removeEventListener('touchstart', window.__boomerTouchStart, true);
                document.removeEventListener('touchend', window.__boomerTouchEnd, true);
            }
            window.__boomerTarget = null;
        }
    }
    });

    // ════════════════════════════════════════════════
    // 13. COSMETIC FILTERING (Element Hiding)
    // ════════════════════════════════════════════════
    function applyCosmeticFiltering() {
        try {
            browser.runtime.sendMessage({
                type: "get_cosmetic_rules",
                host: window.location.hostname
            }).then(response => {
                if (response && response.selectors && response.selectors.length > 0) {
                    let style = document.getElementById('__jusbrowse_cosmetic_style');
                    if (!style) {
                        style = document.createElement('style');
                        style.id = '__jusbrowse_cosmetic_style';
                        (document.head || document.documentElement).appendChild(style);
                    }
                    const css = response.selectors.map(s => {
                        if (!s || s.includes('{') || s.includes('}')) return '';
                        return `${s} { display: none !important; }`;
                    }).join('\n');
                    style.textContent = css;
                }
            }).catch(() => { });
        } catch (e) { }
    }

    if (document.readyState === 'loading') {
        window.addEventListener('DOMContentLoaded', applyCosmeticFiltering, { once: true });
    } else {
        applyCosmeticFiltering();
    }

    // ════════════════════════════════════════════════
    // 14. DYNAMIC AD DETECTION (MutationObserver)
    // ════════════════════════════════════════════════
    const AD_SIGNATURES = [
        'sponsored', 'taboola', 'outbrain', 'ad-container', 'ad-slot',
        'google_ads', 'carbonads', 'ad-unit', 'advertisement'
    ];

    var isRemovingAd = false;

    function isCriticalElement(node) {
        const tag = node.tagName ? node.tagName.toLowerCase() : '';
        return tag === 'html' || tag === 'body' || tag === 'head' ||
               tag === 'frameset' || tag === 'frame' ||
               (node.parentNode && (node.parentNode.tagName || '').toLowerCase() === 'body' && tag === 'div' && node.children && node.children.length > 20);
    }

    function checkAndRemoveAds(node) {
        if (node.nodeType !== 1) return;
        if (isCriticalElement(node)) return;
        if (isRemovingAd) return;

        const id = (node.id || '').toLowerCase();
        const className = (node.className || '');
        const tagName = node.tagName.toLowerCase();

        if (tagName === 'iframe') {
            const src = node.src || '';
            if (AD_SIGNATURES.some(sig => src.includes(sig))) {
                isRemovingAd = true;
                try { node.remove(); } catch(e) {}
                setTimeout(function() { isRemovingAd = false; }, 50);
                return;
            }
        }

        const classStr = typeof className === 'string' ? className.toLowerCase() : '';
        if (AD_SIGNATURES.some(sig => id.includes(sig) || classStr.includes(sig))) {
            if (node.parentNode && node.parentNode.children && node.parentNode.children.length <= 2) return;
            isRemovingAd = true;
            try { node.remove(); } catch(e) {}
            setTimeout(function() { isRemovingAd = false; }, 50);
            return;
        }

        if (node.children && node.children.length < 5) {
            for (let i = node.children.length - 1; i >= 0; i--) {
                checkAndRemoveAds(node.children[i]);
            }
        }
    }

    var obTimer = null;
    var pendingNodes = [];

    function flushAdCheck() {
        var nodes = pendingNodes;
        pendingNodes = [];
        for (var i = 0; i < nodes.length; i++) {
            checkAndRemoveAds(nodes[i]);
        }
    }

    const adObserver = new MutationObserver((mutations) => {
        for (var m = 0; m < mutations.length; m++) {
            var mutation = mutations[m];
            for (var n = 0; n < mutation.addedNodes.length; n++) {
                pendingNodes.push(mutation.addedNodes[n]);
            }
        }
        if (obTimer === null) {
            obTimer = setTimeout(function() {
                obTimer = null;
                flushAdCheck();
            }, 120);
        }
    });

    adObserver.observe(document.documentElement, { childList: true, subtree: true });

    console.log('[JusBrowse] Site Identity Layer initialized. Fingerprinting left to GeckoView engine.');
})();
