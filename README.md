# JusBrowse — The Stealth-First Privacy Browser for Android

[![Status](https://img.shields.io/badge/Status-Alpha%206.3-orange.svg)]()
[![Engine](https://img.shields.io/badge/Engine-GeckoView-blue.svg)]()
[![Privacy](https://img.shields.io/badge/Privacy-Hardened-green.svg)]()
[![License](https://img.shields.io/badge/License-GPL%20v3-red.svg)](LICENSE)

**JusBrowse** is a high-security, privacy-hardened web browser for Android. Built on the **GeckoView** engine (the foundation of Firefox), it is designed to provide "Tor-style" fingerprinting resistance, privacy-safe analytics, and deep engine-level isolation that standard Chromium-based browsers cannot achieve.

> "Privacy is not a checkbox; it’s a war. Every API output can pull off the entire mask."

---

## 🛡️ Core Pillars

- **GeckoView Powered**: Unlike the system WebView, GeckoView allows for total control over the network stack, rendering characteristics, and privacy flags.
- **Fingerprinting Resistance**: Systematic normalization of high-entropy APIs (Canvas, Audio, WebGL, Screen, Locale) to match generic flagship profiles (powered by the JusFake engine).
- **Privacy-Safe Analytics**: Minimalist, opt-out telemetry using a privacy-first Supabase backend. We only track installation and daily streaks to improve the app—no device fingerprinting or browsing data ever leaves your device.
- **Persona Isolation**: Create distinct "Golden Profiles" (e.g., Pixel 8 Pro). Each persona runs in a strictly isolated Gecko session with its own cookie jar and storage context.

---

## 🚀 Key Features

- **Multi-View Workspace**: Draggable, resizable windows in a desktop-like environment.
- **Airlock Media System**: Extract images, videos, and audio from any page into a clean, local gallery.
- **HTTPS-Only**: Native enforcement of encrypted connections at the engine level.
- **Ghost Cookie Jar**: Memory-only cookie storage and per-tab context isolation.
- **Stealth Bridging**: Randomized native-to-JS bridge names to prevent detection by anti-detect scripts.

---

## 📖 Documentation Roadmap

Explore the project's internal documentation:

- **[Architecture Overview](DOCUMENTATION.md)**: Deep dive into the `NetworkSurgeon`, `PrivacyBus`, and `FakeModeManager`.
- **[Installation Guide](INSTALLATION.md)**: How to build from source and run on your device.
- **[Security & Privacy Claims](SECURITY_CLAIMS.md)**: Detailed breakdown of our fingerprinting defenses and EFF benchmark results.
- **[Features & FAQ](FAQ.md)**: Frequently asked questions and feature rundown.
- **[Contributing](CONTRIBUTING.md)**: How to help the project grow.
- **[Roadmap](ROADMAP.md)**: What's coming in Beta and beyond.

---

## 🛠️ Quick Start

### Build from Source
1. Clone the repository.
2. Open in Android Studio (Ladybug or later).
3. Ensure you have the NDK installed (for GeckoView native components).
4. Run `./gradlew assembleDebug` or build directly from Android Studio.

*Detailed instructions in [INSTALLATION.md](INSTALLATION.md).*

---

## ⚖️ License
Licensed under the **GNU General Public License v3.0** or later. See [LICENSE](LICENSE) for details.

---
*Developed by JusDots with passion for a free and private internet.*

