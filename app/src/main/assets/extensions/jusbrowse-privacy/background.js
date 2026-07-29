"use strict";

const BLOCKED_DOMAINS = new Set();
const BLOCKED_PATHS = [
    "/ads.js", "/pagead.js", "/adserving.js",
    "/ad.js", "/adserver.js", "/advertising.js",
    "/track.js", "/tracking.js", "/analytics.js",
    "/pixel.js", "/beacon.js", "/stats.js",
    "/adsbygoogle.js", "/impression.js", "/clicktrack.js",
    "/adclick.js", "/adview.js", "/adload.js",
];
let adBlockEnabled = true;
let loaded = false;

function parseRule(line) {
    line = line.trim();
    if (!line || line.startsWith("!") || line.startsWith("#")) return;
    if (line.startsWith("||") && line.endsWith("^")) {
        const domain = line.substring(2, line.length - 1);
        if (domain && domain.includes(".")) {
            BLOCKED_DOMAINS.add(domain.toLowerCase());
        }
    }
}

async function loadBlocklist() {
    if (loaded) return;
    try {
        const url = browser.runtime.getURL("ublock_filter_list.txt");
        const response = await fetch(url);
        const text = await response.text();
        for (const line of text.split("\n")) {
            parseRule(line);
        }
        loaded = true;
        console.log(`[JusBrowse] Blocklist loaded: ${BLOCKED_DOMAINS.size} domains`);
    } catch (e) {
        console.error("[JusBrowse] Failed to load filter list", e);
    }
}

function isDomainBlocked(host) {
    if (!host) return false;
    if (BLOCKED_DOMAINS.has(host)) return true;
    for (const domain of BLOCKED_DOMAINS) {
        if (host.endsWith("." + domain)) return true;
    }
    return false;
}

function isBlockedUrl(url) {
    try {
        const parsed = new URL(url);
        const host = parsed.hostname.toLowerCase();
        const path = parsed.pathname.toLowerCase();
        if (isDomainBlocked(host)) return true;
        for (const blockedPath of BLOCKED_PATHS) {
            if (path.includes(blockedPath)) return true;
        }
        return false;
    } catch (e) {
        return false;
    }
}

browser.webRequest.onBeforeRequest.addListener(
    (details) => {
        if (!adBlockEnabled) return {};
        try {
            if (details.url.startsWith("resource:") || details.url.startsWith("chrome:") ||
                details.url.startsWith("moz-extension:")) {
                return {};
            }
            const docUrl = details.documentUrl || details.originUrl || "";
            if (docUrl && (docUrl.startsWith("resource:") || docUrl.startsWith("chrome:") ||
                docUrl.startsWith("moz-extension:"))) {
                return {};
            }
            if (isBlockedUrl(details.url)) {
                if (appPort) {
                    try {
                        const parsed = new URL(details.url);
                        appPort.postMessage({
                            type: "report_blocked_tracker",
                            domain: parsed.hostname,
                            url: details.documentUrl || details.originUrl || details.url,
                            tabId: details.tabId
                        });
                    } catch (e) {}
                }
                return { cancel: true };
            }
        } catch (e) {}
        return {};
    },
    { urls: ["<all_urls>"] },
    ["blocking"]
);

let appPort = null;

function handlePortMessage(message) {
    if (message.type === "extract_media") {
        handleMediaExtraction();
    } else if (message.type === "toggle_boomer") {
        browser.tabs.query({}).then(tabs => {
            tabs.forEach(tab => {
                browser.tabs.sendMessage(tab.id, {
                    type: "toggle_boomer",
                    enabled: message.enabled
                }).catch(() => {});
            });
        });
    }
}

function connectToNative() {
    try {
        appPort = browser.runtime.connectNative("jusbrowse");
        appPort.onMessage.addListener(handlePortMessage);
        appPort.onDisconnect.addListener(() => {
            appPort = null;
            setTimeout(connectToNative, 2000);
        });
    } catch (e) {
        setTimeout(connectToNative, 2000);
    }
}
connectToNative();

async function handleMediaExtraction() {
    try {
        const tabs = await browser.tabs.query({ active: true });
        let targets = tabs;
        if (!targets || targets.length === 0) {
            targets = await browser.tabs.query({});
        }
        if (targets.length === 0) return;
        for (const tab of targets) {
            try {
                const response = await browser.tabs.sendMessage(tab.id, { type: "extractMedia" });
                if (response && appPort) {
                    appPort.postMessage({ type: "media_extracted", media: response });
                }
            } catch (e) {}
        }
    } catch (e) {
        console.error("Airlock Error:", e);
    }
}

loadBlocklist();