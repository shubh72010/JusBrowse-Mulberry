"use strict";

// ═══════════════════════════════════════════════════
// AD & TRACKER BLOCKING
// ═══════════════════════════════════════════════════

const HARDCODED_BLOCKED = new Set([
    "doubleclick.net", "googlesyndication.com", "googleadservices.com", "googletagmanager.com", "tagmanager.google.com", "google-analytics.com", "googleanalytics.com", "adservice.google.com", "tpc.googlesyndication.com", "pagead2.googlesyndication.com", "imasdk.googleapis.com", "app-measurement.com", "dai.google.com",
    "amazon-adsystem.com", "aax.amazon-adsystem.com", "aan.amazon.com", "device-metrics-us.amazon.com", "device-metrics-us-2.amazon.com", "mads-eu.amazon.com", "advertising-api-eu.amazon.com", "adtago.s3.amazonaws.com", "analyticsengine.s3.amazonaws.com", "advice-ads.s3.amazonaws.com", "affiliationjs.s3.amazonaws.com",
    "facebook.net", "facebook.com", "instagram.com", "connect.facebook.net", "graph.facebook.com", "tr.facebook.com", "graph.instagram.com", "i.instagram.com",
    "bingads.microsoft.com", "ads.microsoft.com", "adnxs.com", "bat.bing.com", "clarity.ms", "scorecardresearch.com",
    "yahooinc.com", "ads.yahoo.com", "gemini.yahoo.com", "adtech.yahooinc.com",
    "taboola.com", "outbrain.com", "trc.taboola.com", "cdn.taboola.com", "widgets.outbrain.com", "log.outbrain.com", "odb.outbrain.com",
    "criteo.com", "criteo.net", "bidder.criteo.com", "static.criteo.net",
    "yandex.ru", "metrika.yandex.ru", "mc.yandex.ru", "adfox.yandex.ru", "appmetrica.yandex.ru",
    "miui.com", "xiaomi.com", "mistat.xiaomi.com", "hicloud.com", "apple.com", "metrics.icloud.com", "metrics.mzstatic.com", "iadsdk.apple.com", "api-adservices.apple.com", "books-analytics-events.apple.com", "weather-analytics-events.apple.com", "notes-analytics-events.apple.com", "xp.apple.com", "samsungads.com", "smetrics.samsung.com", "oppomobile.com", "realme.com", "realmemobile.com", "lgsmartad.com", "lgappstv.com", "lge.com", "roku.com", "vizio.com", "huawei.com", "ads.huawei.com",
    "rereddit.com", "reddit.com", "widgets.pinterest.com", "ads-dev.pinterest.com", "pinterest.com", "byteoversea.com", "tiktok.com", "appspot.com", "sc-analytics.appspot.com", "quora.com", "vk.com", "snapchat.com",
    "mineralt.io", "crypto-loot.org", "popcash.net", "onclickads.net", "greatis.com", "onclickads.net", "propellerclick.com", "popads.net",
    "cookiebot.com", "cookielaw.org", "trustarc.com", "privacy-center.org", "privacy-mgmt.com", "usercentrics.eu", "impact.com", "partnerstack.com", "refersion.com", "skimresources.com", "viglink.com", "optimizely.com", "dynamicyield.com", "jwpsrv.com", "jwpcdn.com", "fwmrm.net", "connatix.com", "innovid.com", "tremorhub.com", "intercom.io", "driftt.com", "bnc.lt", "appsflyer.com", "adjust.com", "kochava.com", "control.kochava.com"
]);

const BLOCKED_DOMAINS = new Set(HARDCODED_BLOCKED);
const BLOCKED_PATHS = [];
const COSMETIC_RULES = {
    global: new Set(),
    domainSpecific: new Map(),
    exceptions: new Map()
};
let adBlockState = true;
let isBlocklistLoaded = false;

async function loadBlocklist() {
    try {
        const blocklists = ["adblock_list.txt", "easyprivacy.txt"];
        let count = 0;
        for (const filename of blocklists) {
            try {
                const response = await fetch(browser.runtime.getURL(filename));
                const text = await response.text();
                const lines = text.split('\n');
                for (let line of lines) {
                    line = line.trim();
                    if (!line || line.startsWith('!')) continue;

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
            } catch (e) {
                console.error(`Failed to load ${filename}`, e);
            }
        }
        isBlocklistLoaded = true;
        console.log(`Privacy Engine: ${count} rules loaded from ${blocklists.length} blocklists.`);
    } catch (e) {
        console.error("Failed to load blocklists", e);
    }
}

loadBlocklist();

browser.webRequest.onBeforeRequest.addListener(
    (details) => {
        if (adBlockState === false) return {};

        try {
            if (details.url.startsWith("resource:") || details.url.startsWith("chrome:") || details.url.startsWith("moz-extension:")) {
                return {};
            }

            const url = new URL(details.url);
            const host = url.hostname.toLowerCase();
            const path = url.pathname.toLowerCase();

            const docUrl = details.documentUrl || details.originUrl || "";
            if (docUrl && (docUrl.startsWith("resource:") || docUrl.startsWith("chrome:") ||
                docUrl.startsWith("moz-extension:"))) {
                return {};
            }

            if (isDomainBlocked(host) || isPathBlocked(path)) {
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

                return { cancel: true };
            }
        } catch (e) { }
        return {};
    },
    { urls: ["<all_urls>"] },
    ["blocking"]
);

// ═══ NATIVE SYNC ═══
let appPort = null;
const pendingWebAuthn = new Map();

function handlePortMessage(message) {
    if (message.type === "set_adblock") {
        adBlockState = message.enabled;
        browser.storage.local.set({ adBlockEnabled: adBlockState });
    } else if (message.type === "extract_media") {
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
    } else if (message.type === "webauthn_result") {
        var resolver = pendingWebAuthn.get(message.requestId);
        if (resolver) {
            pendingWebAuthn.delete(message.requestId);
            resolver({ result: message.result, error: message.error, errorType: message.errorType });
        }
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

browser.runtime.onMessage.addListener((message, sender, sendResponse) => {
    // Route WebAuthn requests from content script to native port
    if (message.type === "webauthn_request") {
        if (!appPort) {
            sendResponse({ error: "Native bridge not connected", errorType: "NetworkError" });
            return true;
        }
        var requestId = message.requestId;
        pendingWebAuthn.set(requestId, sendResponse);

        // Extract origin from the sender's URL for CredentialManager validation.
        // The origin must match the RP ID (e.g. https://accounts.google.com).
        var origin = "";
        if (sender && sender.url) {
            try {
                var url = new URL(sender.url);
                origin = url.origin;
            } catch (e) {}
        }

        appPort.postMessage({
            type: "webauthn_request",
            subType: message.subType,
            requestId: requestId,
            clientDataHash: message.clientDataHash || "",
            publicKey: message.publicKey
        });
        return true;
    }

    if (message.type === "get_cosmetic_rules") {
        const host = message.host ? message.host.toLowerCase() : "";
        const selectors = new Set(COSMETIC_RULES.global);

        if (host) {
            const parts = host.split(".");
            for (let i = 0; i <= parts.length - 2; i++) {
                const domain = parts.slice(i).join(".");
                const domainRules = COSMETIC_RULES.domainSpecific.get(domain);
                if (domainRules) {
                    domainRules.forEach(s => selectors.add(s));
                }

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

function isDomainBlocked(host) {
    if (!host) return false;
    if (BLOCKED_DOMAINS.has(host)) return true;
    const parts = host.split(".");
    if (parts.length < 2) return false;
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
    try {
        const tabs = await browser.tabs.query({ active: true });
        if (!tabs || tabs.length === 0) {
            const allTabs = await browser.tabs.query({});
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
        browser.tabs.sendMessage(tab.id, { type: "extractMedia" })
            .then(response => {
                if (response && appPort) {
                    appPort.postMessage({ type: "media_extracted", media: response });
                }
            })
            .catch(() => {});
    });
}

browser.storage.local.get(["adBlockEnabled"]).then(res => {
    if (res.adBlockEnabled !== undefined) adBlockState = res.adBlockEnabled;
});
