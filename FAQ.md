# Frequently Asked Questions

### Why not just use Firefox or Brave?
JusBrowse uses the same engine (GeckoView) as Firefox but with stricter defaults — STRICT ETP, HTTPS-only, fingerprinting protection, and container-based cookie isolation. It also offers features like multi-view workspace, Airlock media extraction, and per-container browsing contexts.

### Can I sync my data?
No. All data — history, bookmarks, settings — is stored locally in a Room database. No cloud sync.

### Will you monetize?
No. Non-profit, open-source, no ads, no tracking, no data selling.

### Is it stable enough for daily use?
Alpha stage. The GeckoView engine is stable but the JusBrowse UI and security layers are under active development.

### Can I use Chrome/Firefox extensions?
Only built-in WebExtensions installed via the app's extension manager from addons.mozilla.org.

### Why is the APK so large?
GeckoView adds ~100-150MB. ABI splits (`arm64-v8a` + `armeabi-v7a`) keep per-device downloads smaller. A universal APK is also generated.
