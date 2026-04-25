"use strict";

// ═══════════════════════════════════════════════════
// AGGRESSIVE AD & TRACKER SHIELD
// ═══════════════════════════════════════════════════

// High-priority hardcoded domains for zero-latency blocking
// These are matched as suffixes (e.g., "doubleclick.net" catches "*.doubleclick.net")
const HARDCODED_BLOCKED = new Set([
    // Google / DoubleClick / Analytics
    "doubleclick.net", "googlesyndication.com", "googleadservices.com", "googletagmanager.com", "tagmanager.google.com", "google-analytics.com", "googleanalytics.com", "adservice.google.com", "tpc.googlesyndication.com", "pagead2.googlesyndication.com", "imasdk.googleapis.com", "youtubei.googleapis.com", "app-measurement.com", "dai.google.com",
    // Amazon / AWS S3 Tracker Buckets
    "amazon-adsystem.com", "aax.amazon-adsystem.com", "aan.amazon.com", "device-metrics-us.amazon.com", "device-metrics-us-2.amazon.com", "mads-eu.amazon.com", "advertising-api-eu.amazon.com", "adtago.s3.amazonaws.com", "analyticsengine.s3.amazonaws.com", "advice-ads.s3.amazonaws.com", "affiliationjs.s3.amazonaws.com",
    // Facebook / Instagram
    "facebook.net", "facebook.com", "instagram.com", "connect.facebook.net", "graph.facebook.com", "tr.facebook.com", "graph.instagram.com", "i.instagram.com",
    // Microsoft / Bing
    "bingads.microsoft.com", "ads.microsoft.com", "adnxs.com", "bat.bing.com", "clarity.ms", "scorecardresearch.com",
    // Yahoo / Oath
    "yahooinc.com", "ads.yahoo.com", "gemini.yahoo.com", "adtech.yahooinc.com",
    // Taboola / Outbrain
    "taboola.com", "outbrain.com", "trc.taboola.com", "cdn.taboola.com", "widgets.outbrain.com", "log.outbrain.com", "odb.outbrain.com",
    // Criteo
    "criteo.com", "criteo.net", "bidder.criteo.com", "static.criteo.net",
    // Yandex
    "yandex.ru", "metrika.yandex.ru", "mc.yandex.ru", "adfox.yandex.ru", "appmetrica.yandex.ru",
    // OEM Clusters (Xiaomi, Apple, Huawei, Samsung, Oppo, Realme, LG)
    "miui.com", "xiaomi.com", "mistat.xiaomi.com", "hicloud.com", "apple.com", "metrics.icloud.com", "metrics.mzstatic.com", "iadsdk.apple.com", "api-adservices.apple.com", "books-analytics-events.apple.com", "weather-analytics-events.apple.com", "notes-analytics-events.apple.com", "xp.apple.com", "samsungads.com", "smetrics.samsung.com", "oppomobile.com", "realme.com", "realmemobile.com", "lgsmartad.com", "lgappstv.com", "lge.com", "roku.com", "vizio.com", "huawei.com", "ads.huawei.com",
    // Social / Tracking Failures (Reddit, Pinterest, TikTok)
    "rereddit.com", "reddit.com", "widgets.pinterest.com", "ads-dev.pinterest.com", "pinterest.com", "byteoversea.com", "tiktok.com", "appspot.com", "sc-analytics.appspot.com", "quora.com", "vk.com", "snapchat.com",
    // Cryptominers & Malvertising
    "mineralt.io", "crypto-loot.org", "popcash.net", "onclickads.net", "greatis.com", "onclickads.net", "propellerclick.com", "popads.net",
    // Consent / Affiliate / A/B / Video
    "cookiebot.com", "cookielaw.org", "trustarc.com", "privacy-center.org", "privacy-mgmt.com", "usercentrics.eu", "impact.com", "partnerstack.com", "refersion.com", "skimresources.com", "viglink.com", "optimizely.com", "dynamicyield.com", "jwpsrv.com", "jwpcdn.com", "fwmrm.net", "connatix.com", "innovid.com", "tremorhub.com", "intercom.io", "driftt.com", "bnc.lt", "appsflyer.com", "adjust.com", "kochava.com", "control.kochava.com"
]);

const BLOCKED_DOMAINS = new Set(HARDCODED_BLOCKED);
const BLOCKED_PATHS = [];
const COSMETIC_RULES = {
    global: new Set(),
    domainSpecific: new Map(), // domain -> Set
    exceptions: new Map()      // domain -> Set
};
let adBlockState = true;
let advancedAdBlockState = false;
let isBlocklistLoaded = false;

// Initialize the blocklist dynamically
async function loadBlocklist() {
    try {
        const response = await fetch(browser.runtime.getURL("adblock_list.txt"));
        const text = await response.text();
        const lines = text.split('\n');
        let count = 0;
        for (let line of lines) {
            line = line.trim();
            if (!line || line.startsWith('!')) continue;

            // ═══ COSMETIC FILTERING (ELEMENT HIDING) ═══
            if (line.includes('##') || line.includes('#@#')) {
                const isException = line.includes('#@#');
                const separator = isException ? '#@#' : '##';
                const parts = line.split(separator);
                const domains = parts[0] ? parts[0].split(',') : [''];
                const selector = parts[1];

                if (!selector || selector.startsWith('+js(')) continue;

                for (let domain of domains) {
                    domain = domain.trim().toLowerCase();
                    if (isException) {
                        if (!COSMETIC_RULES.exceptions.has(domain)) COSMETIC_RULES.exceptions.set(domain, new Set());
                        COSMETIC_RULES.exceptions.get(domain).add(selector);
                    } else {
                        if (domain === '') {
                            COSMETIC_RULES.global.add(selector);
                        } else {
                            if (!COSMETIC_RULES.domainSpecific.has(domain)) COSMETIC_RULES.domainSpecific.set(domain, new Set());
                            COSMETIC_RULES.domainSpecific.get(domain).add(selector);
                        }
                    }
                }
                count++;
                continue;
            }

            if (line.startsWith('#')) continue;
            
            const cleanLine = line.split('$')[0].toLowerCase();
            if (cleanLine.startsWith('||') && cleanLine.endsWith('^')) {
                BLOCKED_DOMAINS.add(cleanLine.slice(2, -1));
                count++;
            } else if (cleanLine.startsWith('/')) {
                if (cleanLine.length > 3) {
                    BLOCKED_PATHS.push(cleanLine);
                    count++;
                }
            } else if (cleanLine.includes('.')) {
                BLOCKED_DOMAINS.add(cleanLine);
                count++;
            }
        }
        isBlocklistLoaded = true;
        console.log(`Privacy Engine: ${count} rules loaded. Base protection set is ready.`);
    } catch (e) {
        console.error("Critical: Failed to load adblock_list.txt", e);
    }
}

loadBlocklist();

// ═══ REQUEST BLOCKING ═══
browser.webRequest.onBeforeRequest.addListener(
    (details) => {
        // ALWAYS check blocking logic if adBlockState is on (default true)
        if (adBlockState === false) return {};

        try {
            // Don't block the app's own internal resources
            if (details.url.startsWith("resource:") || details.url.startsWith("chrome:") || details.url.startsWith("moz-extension:")) {
                return {};
            }

            const url = new URL(details.url);
            const host = url.hostname.toLowerCase();
            const path = url.pathname.toLowerCase();

            // Check domain blocklist or path rules
            if (isDomainBlocked(host) || isPathBlocked(path)) {
                // Report via native port to reach Kotlin
                if (appPort) {
                    try {
                        appPort.postMessage({
                            type: "report_blocked_tracker",
                            domain: host,
                            url: details.documentUrl || details.originUrl || details.url,
                            tabId: details.tabId
                        });
                    } catch (err) { }
                }
                
                // standard blocking (cancel: true is more effective than data: redirects for most tests)
                return { cancel: true };
            }
        } catch (e) { }
        return {};
    },
    { urls: ["<all_urls>"] },
    ["blocking"]
);

// ═══ HEADER STRIPPING (Privacy Hardening) ═══
const STRIP_HEADERS = new Set([
    "x-requested-with", "x-client-data", "sec-ch-ua", "sec-ch-ua-mobile", "sec-ch-ua-platform", "sec-ch-ua-full-version-list"
]);

browser.webRequest.onBeforeSendHeaders.addListener(
    (details) => {
        const headers = details.requestHeaders.filter(h => !STRIP_HEADERS.has(h.name.toLowerCase())).map(header => {
            if (header.name.toLowerCase() === 'accept-language') {
                return { name: 'Accept-Language', value: 'en-US,en;q=0.9' };
            }
            return header;
        });
        
        // Inject GPC signal
        headers.push({ name: 'Sec-GPC', value: '1' });
        
        return { requestHeaders: headers };
    },
    { urls: ["<all_urls>"] },
    ["blocking", "requestHeaders"]
);

// ═══ CSP INJECTION (Advanced ADBlock) ═══
browser.webRequest.onHeadersReceived.addListener(
    (details) => {
        if (!advancedAdBlockState) return {};
        
        const responseHeaders = details.responseHeaders;
        const contentTypeHeader = responseHeaders.find(h => h.name.toLowerCase() === 'content-type');
        const contentType = (contentTypeHeader ? contentTypeHeader.value : '').toLowerCase();
        
        // Only inject CSP on HTML documents
        if (!contentType.includes('text/html') && !contentType.includes('application/xhtml')) {
            return {};
        }

        // Check if site already has CSP (respect their choices to avoid breakage)
        const existingCSP = responseHeaders.find(h => h.name.toLowerCase() === 'content-security-policy');
        if (existingCSP) return {};

        const cspHeader = [
            "default-src 'self'",
            "script-src 'self' 'unsafe-inline' 'unsafe-eval' https:",
            "style-src 'self' 'unsafe-inline' https:",
            "img-src 'self' data: https:",
            "font-src 'self' data: https:",
            "connect-src 'self' https:",
            "object-src 'none'",
            "base-uri 'self'",
            "frame-ancestors 'self'",
            "form-action 'self' https:",
            "frame-src 'self' https:",
            "media-src 'self' https:"
        ].join('; ');
        
        responseHeaders.push({
            name: 'Content-Security-Policy',
            value: cspHeader
        });
        
        return { responseHeaders: responseHeaders };
    },
    { urls: ["<all_urls>"], types: ["main_frame", "sub_frame"] },
    ["blocking", "responseHeaders"]
);

// ═══ RESPONSE HEADER STRIPPING (Strip tracker cookies/analytics) ═══
browser.webRequest.onHeadersReceived.addListener(
    (details) => {
        if (!adBlockState) return {};

        try {
            const url = new URL(details.url);
            const host = url.hostname.toLowerCase();

            // ONLY strip headers if the domain is a known tracker
            if (isDomainBlocked(host)) {
                const stripHeaders = ['set-cookie', 'x-analytics', 'x-tracking-id', 'x-ad-id'];
                const responseHeaders = details.responseHeaders.filter(h => {
                    return !stripHeaders.includes(h.name.toLowerCase());
                });
                return { responseHeaders: responseHeaders };
            }
        } catch (e) { }
        return {};
    },
    { urls: ["<all_urls>"] },
    ["blocking", "responseHeaders"]
);

// ═══ NATIVE SYNC ═══
let appPort = null;
function connectToNative() {
    try {
        appPort = browser.runtime.connectNative("jusbrowse");
        appPort.onMessage.addListener((message) => {
            if (message.type === "set_adblock") {
                adBlockState = message.enabled;
                browser.storage.local.set({ adBlockEnabled: adBlockState });
            } else if (message.type === "extract_media") {
                handleMediaExtraction();
            } else if (message.type === "set_advanced_adblock") {
                advancedAdBlockState = message.enabled;
                browser.storage.local.set({ advancedAdBlockEnabled: advancedAdBlockState });
            } else if (message.type === "toggle_boomer") {
                // Forward Boomer Mode toggle to ALL tabs
                browser.tabs.query({}).then(tabs => {
                    tabs.forEach(tab => {
                        browser.tabs.sendMessage(tab.id, { 
                            type: "toggle_boomer", 
                            enabled: message.enabled 
                        }).catch(() => {}); // Ignore errors for tabs without content scripts
                    });
                });
            }
        });
        appPort.onDisconnect.addListener(() => {
            appPort = null;
            setTimeout(connectToNative, 2000);
        });
    } catch (e) {
        setTimeout(connectToNative, 2000);
    }
}
connectToNative();

// ═══ CONTENT SCRIPT MESSAGING ═══
browser.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === "get_cosmetic_rules") {
        const host = message.host ? message.host.toLowerCase() : "";
        const selectors = new Set(COSMETIC_RULES.global);
        
        // Add domain-specific rules
        if (host) {
            const parts = host.split(".");
            for (let i = 0; i <= parts.length - 2; i++) {
                const domain = parts.slice(i).join(".");
                const domainRules = COSMETIC_RULES.domainSpecific.get(domain);
                if (domainRules) {
                    domainRules.forEach(s => selectors.add(s));
                }
                
                // Remove exceptions
                const domainExceptions = COSMETIC_RULES.exceptions.get(domain);
                if (domainExceptions) {
                    domainExceptions.forEach(s => selectors.delete(s));
                }
            }
        }
        
        sendResponse({ selectors: Array.from(selectors) });
        return true;
    } else if (message.type === "is_tracker_domain") {
        const host = message.domain ? message.domain.toLowerCase() : "";
        sendResponse({ isTrackerDomain: isDomainBlocked(host) });
        return true;
    }
});

// ═══ HELPERS ═══
function isDomainBlocked(host) {
    if (!host) return false;
    
    // Check full domain first
    if (BLOCKED_DOMAINS.has(host)) return true;
    
    // Suffix matching (catch subdomains)
    const parts = host.split(".");
    if (parts.length < 2) return false;
    
    // Try progressively shorter suffixes (e.g. a.b.c.com -> b.c.com -> c.com)
    for (let i = 1; i < parts.length - 1; i++) {
        const domain = parts.slice(i).join(".");
        if (BLOCKED_DOMAINS.has(domain)) return true;
    }
    return false;
}

function isPathBlocked(path) {
    if (!path) return false;
    for (const p of BLOCKED_PATHS) {
        if (path.includes(p)) return true;
    }
    return false;
}

async function handleMediaExtraction() {
    console.log("Airlock: Starting media extraction...");
    try {
        // Use active: true instead of WINDOW_ID_CURRENT which is unreliable in background scripts
        const tabs = await browser.tabs.query({ active: true });
        console.log(`Airlock: Found ${tabs ? tabs.length : 0} active tabs.`);
        
        if (!tabs || tabs.length === 0) {
            // Fallback: Query all tabs if no active tab found
            const allTabs = await browser.tabs.query({});
            console.log(`Airlock Fallback: Found ${allTabs.length} total tabs.`);
            if (allTabs.length > 0) {
                broadcastToTabs(allTabs);
            }
            return;
        }
        
        broadcastToTabs(tabs);
    } catch (e) {
        console.error("Airlock Error:", e);
    }
}

function broadcastToTabs(tabs) {
    tabs.forEach(tab => {
        console.log(`Airlock: Sending extractMedia to tab ${tab.id}`);
        browser.tabs.sendMessage(tab.id, { type: "extractMedia" })
            .then(response => {
                if (response) {
                    console.log(`Airlock: Received media from tab ${tab.id}`);
                    if (appPort) {
                        appPort.postMessage({ type: "media_extracted", media: response });
                    }
                } else {
                    console.warn(`Airlock: Empty response from tab ${tab.id}`);
                }
            })
            .catch(err => {
                console.error(`Airlock: Failed to send to tab ${tab.id}`, err);
            });
    });
}

// Ensure toggle persists
browser.storage.local.get(["adBlockEnabled", "advancedAdBlockEnabled"]).then(res => {
    if (res.adBlockEnabled !== undefined) adBlockState = res.adBlockEnabled;
    if (res.advancedAdBlockEnabled !== undefined) advancedAdBlockState = res.advancedAdBlockEnabled;
});
