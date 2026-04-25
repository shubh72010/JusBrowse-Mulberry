# JusBrowse Alpha 6 (v0.0.6-3) - The Gecko Migration

Alpha 6 marks the most significant milestone in JusBrowse history: the complete abandonment of the system WebView in favor of a custom-integrated **GeckoView** engine.

## 🦎 Why GeckoView?
Android's default WebView is inherently "leaky." It shares state with the system, exposes identifiable hardware signatures that can't be spoofed, and lacks the granular control required for a true stealth browser. By switching to GeckoView (the engine behind Firefox), we've gained:
*   **Isolated Contexts**: True multi-persona sandboxing at the engine level.
*   **Deep Spoofing**: The ability to override low-level browser characteristics (Canvas, WebGL, Audio) via engine flags and WebExtensions.
*   **Zero Leakage**: Total control over the network stack and telemetry.

## 🚀 Key Changes in v0.0.6-3

### 1. New Engine Architecture
*   **GeckoRuntime Integration**: Centralized engine management in `BrowserApplication`.
*   **Independent Windows**: Tabs now run as fully isolated GeckoSessions.
*   **WebExtension Support**: Implementation of a specialized privacy extension for ad-blocking and header surgery.

### 2. Fingerprinting Hardening (The Alpha 6 Polish)
*   **~16 Bits Reduction**: Advanced entropy reduction across all detectable APIs.
*   **Persona-Based Isolation**: Moving beyond simple Incognito mode to distinct, persistent personas that websites can't link together.

### 3. Follian Protocol (Initial Beta)
*   A new "Ultra-Stealth" mode that pushes the browser into the most restrictive, high-anonymity state possible, mimicking the behavior of specialized research browsers.

### 4. Build Optimization
*   **R8 Full Mode**: Aggressive code shrinking to counteract the size of the Gecko engine.
*   **ProGuard Specialization**: Stripping unused Gecko/Media3 native code.

## 📋 Technical Upgrade Path
*   **Native Interception**: Migrated from `shouldInterceptRequest` logic to GeckoView's internal delegate system.
*   **Cookie Handling**: Replaced manual cookie sync with Gecko's `contextId` isolation.
*   **UI Bridge**: Rewrote the Address Bar and Tab logic to interface with `GeckoSession`.

---
*JusBrowse Alpha 6: Privacy is not a checkbox, it's a war.*
