# JusBrowse Mulberry — Privacy-First Android Browser

[![Engine](https://img.shields.io/badge/Engine-GeckoView%20Nightly-blue.svg)]()
[![API](https://img.shields.io/badge/MinSdk-28%20%7C%20TargetSdk-37-green.svg)]()
[![Privacy](https://img.shields.io/badge/Privacy-Hardened-green.svg)]()
[![License](https://img.shields.io/badge/License-GPL%20v3-red.svg)](LICENSE)

A single-module Android browser built on **GeckoView Nightly** (not WebView). Storage-first architecture: disk is source of truth, RAM is temporary workspace.

---

## Engine & Networking

- **GeckoView Nightly 154.0** — full browser engine with STRICT Enhanced Tracking Protection, HTTPS-Only mode, fingerprinting protection, and letterboxing
- **DNS over HTTPS** — 23 preset providers (Cloudflare, Quad9, NextDNS, AdGuard, etc.) plus custom URL
- **OkHttp 5.4** — shared HTTP client with system CA validation (no cert pinning), memory-only cookie isolation
- **Ghost Cookie Jar** — per-container cookies in RAM only, never persisted

## Privacy & Security

- **Built-in WebExtension** (`jusbrowse-privacy`) — ad blocking, tracker blocking, native messaging bridge
- **Per-container isolation** — 5 named containers (default, work, personal, banking, sandbox) via GeckoView `contextId`, each with separate cookies/cache/storage
- **Post body sanitization** — strips analytics/tracking payloads from outgoing POST requests
- **Download validation** — MIME checks, APK/JS warnings, VirusTotal + Koodous API scanning
- **Screenshot protection** — toggleable `FLAG_SECURE` window flag
- **WebAuthn/passkey support** — Android Credential Manager + legacy FIDO2 fallback
- **Cookie consent dialog blocking** — engine-level
- **Protection whitelist** — comma-separated domain exclusions

## UI & Themes

- **7 screens**: Browser, Bookmarks, History, Downloads, Settings, Extensions, Extension Detail
- **Multi-view workspace** — draggable, resizable tab windows in desktop-like layout
- **16 theme presets** (Vivaldi Red, Ocean Blue, Nord Ice, Dracula, Cyberpunk, etc.) + custom hex color
- **6 animated background presets** + custom start page wallpaper with adjustable blur
- **AMOLED black mode**, font selector, app icon badges, pill menu with gesture engine
- **10+ UI configs**: tab chip height, active tab style, scrim darkness, pill position/width/opacity, toolbar position, compact mode

## Media

- **Airlock media system** — extract images, video, and audio from any page into a local gallery
- **Airlock Vault** — private app-local storage with WebP conversion and `.nomedia` isolation
- **ExoPlayer (Media3)** for video/audio playback
- **Coil** for async image loading

## Data & Storage

- **Room DB** (v9) — bookmarks, history, downloads, extension registry, per-origin settings
- **Protobuf snapshots** — binary-serialized tab sessions and workspace state saved to disk
- **Encrypted SharedPreferences** — VirusTotal/Koodous API keys
- **Tab lifecycle**: Active → Suspended → Serialized → Evicted (hard cap: 1 active + 2 suspended, 150MB GeckoView)
- **Disk cache** — configurable 20–2500 MB with clear button

## Build

```bash
./gradlew assembleDebug          # debug (no R8)
./gradlew assembleRelease        # release (R8 fullMode, minify, obfuscation, shrink)
./gradlew testDebugUnitTest      # 4 unit tests
```

MinSdk 28, targetSdk 37, ABI split: `arm64-v8a` + `armeabi-v7a` with universal APK. GeckoView Nightly ~150MB downloaded on first sync. NDK required.

## Update Checker

On launch, checks `api.github.com/repos/shubh72010/JusBrowse-Strait/releases/latest` for newer releases. Shows an in-app dialog and a "Check for updates" option in Settings.

---

## License

GNU General Public License v3.0 or later. See [LICENSE](LICENSE).

*Built by JusDots.*
