# AGENTS.md

## Project

**JusBrowse Strait** — single-module (`:app`) Android browser using **GeckoView Nightly** (not WebView), Kotlin 2.4.10, Jetpack Compose + Material 3, AGP 9.3.0. Gradle 9.6.1. MinSdk 28, compileSdk/targetSdk 37. App ID: `com.jusdots.jusbrowse`.

Remotes: `mulberry` (shubh72010/JusBrowse-Mulberry, upstream), `geckoview` (shubh72010/JusBrowse-GeckoView, fork source).

## Build

```bash
./gradlew assembleDebug          # debug (no R8)
./gradlew assembleRelease        # release (R8 fullMode, minify, obfuscation, shrink resources)
./gradlew testDebugUnitTest      # unit tests (JUnit 4)
./gradlew clean
```

CI (`.github/workflows/build.yml`): `lintDebug` → `testDebugUnitTest` → `assembleDebug` → `assembleRelease`, all `--no-daemon`. Java 17 (Temurin).

**Lint is disabled** (`app/build.gradle.kts:98`) — the CI `lintDebug` step is a no-op.

**Signing**: Release signs with debug keystore (`signingConfigs.getByName("debug")`). Android Studio injected signing paths (Windows default) are overridden in `settings.gradle.kts:8-14` to `~/.android/debug.keystore`.

**Memory** (13GB RAM + 8GB swap): `org.gradle.jvmargs=-Xmx3g`, `kotlin.daemon.jvm.options=-Xmx2g`. If OOM-killed, run with `--no-daemon`.

**First sync**: Downloads GeckoView Nightly ~150MB from `https://maven.mozilla.org/maven2/`. NDK required.

## Release quirks

- AAR metadata check disabled — GeckoView nightly demands SDK 37.1+, not installed (`app/build.gradle.kts:94`)
- `validateSigningRelease` disabled
- ABI split: `arm64-v8a` + `armeabi-v7a` only, `isUniversalApk = true`
- R8 fullMode strips `android.util.Log`, `Throwable.printStackTrace()`, and Kotlin null checks (`proguard-rules.pro`)
- Resources exclude GeckoView baseline profiles and `androidx.profileinstaller` metadata
- Only `en` locale resources included

## Generated code

- Protobuf: `app/src/main/proto/jusbrowse/snapshot.proto` → Java under `build/generated/`
- Room DAOs: KSP, schema exports at `app/schemas/` (not tracked in git)
- Compose compiler: `kotlin-compose` plugin (Kotlin 2.4.0+), no separate dep
- KSP `2.3.10` must stay compatible with Kotlin version

## Tests

4 test files in `app/src/test/` (JUnit 4, no MockK wired yet):
- `storage/CacheDeduplicatorTest` — ref count, eviction, dedup
- `security/DownloadValidatorTest` — MIME checks, extension blocking, APK/JS warnings
- `security/PostBodySanitizerTest` — tracker endpoint blocking, analytics stripping
- `ui/runtime/PrecomputedAnimationTest` — overshoot, spring, bounce, lerp

MockK (`1.13.16`) declared in version catalog but **not in `build.gradle.kts` deps**. No `androidTest` sources exist.

## Architecture

Package: `com.jusdots.jusbrowse`

```
data/         — models/, database/ (Room DB v9 with MIGRATION_7_8, MIGRATION_8_9), repository/
lifecycle/    — MemoryBudgetController, TabLifecycleManager
security/     — NetworkSurgeon, GhostCookieJar, ContentBlocker, DnsOverHttps, SurgicalBridge, BrowserMessageDelegate, PostBodySanitizer, DownloadValidator, ExtensionManager, GeckoSessionFactory, CredentialManagerHandler, etc. (23 files)
storage/      — CacheDeduplicator, StorageWritePolicyEngine, TabSnapshotStorage
ui/           — components/ (21 files), screens/ (7), runtime/ (9 — animations, caching, frozen state), delegate/, theme/, viewmodel/BrowserViewModel.kt (~1700 lines)
utils/        — AirlockVaultManager, MediaExtractor
```

**Strait** = storage-first: disk is source of truth, RAM is temporary workspace.
**Persona isolation**: each persona = unique GeckoView `contextId` — separate cookies/storage.
**Tab lifecycle**: Active → Suspended → Serialized → Evicted (hard cap: 1 active + 2 suspended, 150MB GeckoView).
**Ghost Cookie Jar**: memory-only cookies for sensitive personas (never persisted).
**NetworkSurgeon**: OkHttp with system CA validation — no certificate pinning (previous fake pins caused SSL failures).

Key entrypoints:
- `BrowserApplication.kt` — `GeckoRuntime` init, WebExtension loading, Room DB singleton
- `StraitArchitecture.kt` — core orchestrator (lifecycle, storage, budget)
- `MainActivity.kt` — Compose entry, WebAuthn, download receiver
- `BrowserViewModel.kt` — ~1700 lines, main UI state coordinator

Manifest: `allowBackup=false`, `usesCleartextTraffic=false`, `networkSecurityConfig` enforced. `launchMode="singleTask"`.

## GeckoView specifics

- Uses Nightly channel (`org.mozilla.geckoview:geckoview-nightly`)
- Runtime configured with: STRICT ETP, ACCEPT_FIRST_PARTY_AND_ISOLATE_OTHERS cookies, HTTPS-only, Cloudflare DoH (TRR_MODE_FIRST), fingerprinting protection, LNA (letterboxing) enabled
- Built-in WebExtension at `resource://android/assets/extensions/jusbrowse-privacy/`
- WebExtension ↔ native bridge via `BrowserMessageDelegate` ("jusbrowse" namespace)
- `GeckoSessionSettings` use `contextId` for persona isolation
- Some Nightly-only API methods used: `setLnaEnabled`, `setLnaBlocking`, `setFingerprintingProtection`, `setCookieBehaviorOptInPartitioning`

## Dev environment (Flatpak)

Android Studio runs inside Flatpak. A wrapper `~/.local/bin/jb-python3.12` bridges host Python for Gradle's `mach` subprocesses. If the venv gets recreated by `mach`:

```bash
ln -sf ~/.local/bin/jb-python3.12 /path/to/venv/bin/python3.12
```

`GRADLE_MACH_PYTHON` and `PYTHON3=python3.12` are set via `mozconfig`.

## OpenCode skills

23 project-local skills at `.opencode/skills/` + the `agent-skills/` plugin (gitignored). Skill loading guidance lives in `~/.config/opencode/AGENTS.md` — start there for MCP routing and skill selection policy. The two key environment constraints (Flatpak Python bridge, Gradle OOM settings) logged there; update via `manage_adr` via codebase-memory-mcp if changed.

Docs: `DOCUMENTATION.md`, `JusBrowse-Strait-Project-Specification.txt`, `ALPHA6_UPGRADE.md`, `INSTALLATION.md`.
