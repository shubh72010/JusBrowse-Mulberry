# AGENTS.md

## Project

**JusBrowse Mulberry** (fork of `Strait`) — single-module (`:app`) Android browser using **GeckoView Nightly**, Kotlin 2.4.10, Jetpack Compose + Material 3, AGP 9.3.0, Gradle 9.6.1. MinSdk 28, compileSdk/targetSdk 37. App ID: `com.jusdots.jusbrowse`.

Remotes: `mulberry` (shubh72010/JusBrowse-Mulberry, upstream), `geckoview` (shubh72010/JusBrowse-GeckoView, fork source).

## Build & test

```bash
./gradlew assembleDebug          # debug (no R8)
./gradlew assembleRelease        # release (R8 fullMode, minify, obfuscation, shrink resources)
./gradlew testDebugUnitTest      # 4 tests (JUnit 4, no MockK wired)
./gradlew clean
```

CI (`.github/workflows/build.yml`, Java 17 Temurin): `lintDebug` → `testDebugUnitTest` → `assembleDebug` → `assembleRelease`, all `--no-daemon`.

**Lint is disabled** (`app/build.gradle.kts:98-100`) — CI `lintDebug` runs but is a no-op.

**KSP `2.3.10` must stay compatible with Kotlin version** (currently `2.4.10`).

**Memory**: `org.gradle.jvmargs=-Xmx3g`, `kotlin.daemon.jvm.options=-Xmx2g`, `org.gradle.parallel=false`.

## Release quirks

- Signs with debug keystore (`signingConfigs.getByName("debug")`); Windows default signing paths overridden in `settings.gradle.kts:8-14` to `~/.android/debug.keystore`
- AAR metadata check disabled — GeckoView nightly demands SDK 37.1+ (`app/build.gradle.kts:94-96`)
- `validateSigningRelease` disabled
- ABI split: `arm64-v8a` + `armeabi-v7a` only, `isUniversalApk = true`
- R8 strips `android.util.Log`, `Throwable.printStackTrace()`, Kotlin null checks (`app/proguard-rules.pro`)
- Packaging excludes GeckoView baseline profiles and `androidx.profileinstaller` metadata
- Only `en` locale resources
- First sync downloads GeckoView Nightly ~150MB from `https://maven.mozilla.org/maven2/`; NDK required

## Generated code

- Protobuf: `app/src/main/proto/jusbrowse/snapshot.proto` → Java under `build/generated/`
- Room DAOs: KSP, schema exports at `app/schemas/` (not tracked in git)
- Compose compiler: `kotlin-compose` plugin (Kotlin 2.4.0+), no separate dep

## Architecture

Package: `com.jusdots.jusbrowse`

```
data/         — models/, database/ (Room DB v9, MIGRATION_7_8, MIGRATION_8_9), repository/
lifecycle/    — MemoryBudgetController, TabLifecycleManager
security/     — 23 files: NetworkSurgeon, GhostCookieJar, ContentBlocker, DnsOverHttps,
                SurgicalBridge, BrowserMessageDelegate, PostBodySanitizer, DownloadValidator,
                ExtensionManager, GeckoSessionFactory, CredentialManagerHandler, etc.
storage/      — CacheDeduplicator, StorageWritePolicyEngine, TabSnapshotStorage
ui/           — components/ (21 files), screens/ (7), runtime/ (9 — animations, caching, frozen),
                delegate/, theme/, viewmodel/BrowserViewModel.kt (~1700 lines)
utils/        — AirlockVaultManager, MediaExtractor, UpdateChecker
```

Key entrypoints: `BrowserApplication.kt` (GeckoRuntime init, WebExtension loading, Room DB singleton), `StraitArchitecture.kt` (core orchestrator), `MainActivity.kt` (Compose entry, WebAuthn, downloads), `BrowserViewModel.kt` (main UI state).

* `buildConfig = true` is set in `app/build.gradle.kts` — `BuildConfig.VERSION_NAME` is used by `UpdateChecker` in `BrowserViewModel` init to check GitHub releases for updates.
* Update dialog shown as `AlertDialog` in `BrowserScreen` when `viewModel.updateInfo` is non-null.

**Strait** = storage-first (disk is source of truth, RAM is temporary).
**Persona isolation**: each persona → unique GeckoView `contextId` → separate cookies/storage.
**Tab lifecycle**: Active → Suspended → Serialized → Evicted (hard cap: 1 active + 2 suspended, 150MB GeckoView).
**Ghost Cookie Jar**: memory-only cookies for sensitive personas (never persisted).
**NetworkSurgeon**: OkHttp with system CA validation — no certificate pinning.
**WebExtension**: built-in at `resource://android/assets/extensions/jusbrowse-privacy/`, bridge via `BrowserMessageDelegate` ("jusbrowse" namespace).
**Manifest**: `allowBackup=false`, `usesCleartextTraffic=false`, `networkSecurityConfig` enforced, `launchMode="singleTask"`.

## Dev environment (Flatpak)

Android Studio runs inside Flatpak. A wrapper `~/.local/bin/jb-python3.12` bridges host Python for Gradle's `mach` subprocesses. If `mach` recreates the venv:

```bash
ln -sf ~/.local/bin/jb-python3.12 /path/to/venv/bin/python3.12
```

`GRADLE_MACH_PYTHON` and `PYTHON3=python3.12` are set via `mozconfig`.

## OpenCode skills

23 project-local skills at `.opencode/skills/` + `agent-skills/` (gitignored). Skill guidance in `~/.config/opencode/AGENTS.md`.

## Tests

4 test files (`app/src/test/`), JUnit 4, no MockK wired:
- `storage/CacheDeduplicatorTest` — ref count, eviction, dedup
- `security/DownloadValidatorTest` — MIME checks, extension blocking, APK/JS warnings
- `security/PostBodySanitizerTest` — tracker endpoint blocking, analytics stripping
- `ui/runtime/PrecomputedAnimationTest` — overshoot, spring, bounce, lerp

MockK (`1.13.16`) declared in version catalog but **not in `app/build.gradle.kts` deps**. No `androidTest` sources.
