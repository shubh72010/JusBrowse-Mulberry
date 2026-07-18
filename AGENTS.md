# AGENTS.md

## Project Identity

**JusBrowse Strait** — single-module (`:app`) Android browser using **GeckoView Nightly** (not WebView), Kotlin 2.4.10, Jetpack Compose + Material 3, AGP 9.3.0. MinSdk 28, compileSdk 37. Locale-filtered to `en` only. ABI splits: only `arm64-v8a` + `armeabi-v7a` (no x86 — won't run on emulator without adding ABI).

Repo: `shubh72010/JusBrowse-Mulberry` (remote: `mulberry`).

## Build & Test

```bash
./gradlew assembleDebug          # Debug build (no R8)
./gradlew assembleRelease        # Release build (R8 fullMode, shrinking, obfuscation)
./gradlew test                   # Unit tests (38 tests, all passing)
./gradlew clean                  # Clean build artifacts
```

**Memory**: Dev machine is 13GB RAM + 8GB swap. Gradle daemon at `-Xmx3g`, Kotlin daemon at `-Xmx2g`. If daemon is OOM-killed, run with `--no-daemon` or restart manually.

**First sync is slow**: GeckoView Nightly ~150MB is fetched from `https://maven.mozilla.org/maven2/`. Ensure NDK is installed (GeckoView native components).

**Flatpak fix**: Android Studio runs inside Flatpak. A wrapper `~/.local/bin/jb-python3.12` bridges host Python into the sandbox for Gradle's `mach` subprocesses. If the venv gets recreated by `mach`, re-symlink: `ln -sf ~/.local/bin/jb-python3.12 /path/to/venv/bin/python3.12`

**Generated code**: Protobuf (from `app/src/main/proto/jusbrowse/snapshot.proto`), Room DAOs via KSP (schema exports at `app/schemas/`), Compose compiler via `kotlin-compose` plugin (no separate compiler dep, Kotlin 2.4.0+).

**Tests**: 38 unit tests across 4 test suites — all passing:
- `CacheDeduplicatorTest` (8 tests) — ref count, eviction, dedup, stale invalidation
- `DownloadValidatorTest` (10 tests) — MIME checks, extension blocking, APK/JS warnings
- `PostBodySanitizerTest` (6 tests) — tracker endpoint blocking, analytics stripping
- `PrecomputedAnimationTest` (12 tests) — overshoot, spring, bounce, lerp, smoothstop

**Release build quirks**:
- AAR metadata check disabled: `tasks.matching { it.name.startsWith("check") && it.name.endsWith("AarMetadata") }.configureEach { enabled = false }` (GeckoView nightly SDK 37.1 not locally available; compileSdk=37).
- Lint disabled for release: `lint.checkReleaseBuilds = false`, `lint.abortOnError = false`.
- `validateSigningRelease` disabled — release uses debug keystore for now.
- Android Studio injects `externalOverride` signing config pointing to Windows path via `-Pandroid.injected.signing.*` properties. Fixed in `settings.gradle.kts` by overriding these properties before AGP reads them (maps to `~/.android/debug.keystore`).
- ABI split to `arm64-v8a` + `armeabi-v7a` only (no `universal`).
- R8 full mode: `android.enableR8.fullMode=true`.

## Architecture Highlights

- **Strait** = storage-first architecture: disk is source of truth, RAM is temporary workspace
- **Security layers**: `NetworkSurgeon`, `GhostCookieJar`, `ContentBlocker` (EasyPrivacy), `DnsOverHttps` (Cloudflare), `SurgicalBridge` (randomized native↔JS bridge names), `BrowserMessageDelegate` (WebExtension bridge)
- **Persona isolation**: each persona = unique GeckoView `contextId` — separate cookies/storage per identity
- **Tab lifecycle state machine**: Active → Suspended → Serialized → Evicted (managed by `TabLifecycleManager`, enforced by `MemoryBudgetController` — hard cap: 1 active tab RAM, 2 suspended, 150MB GeckoView budget)
- **Ghost Cookie Jar**: cookies kept in memory only for sensitive personas (never persisted)

### Key entrypoints

| File | Role |
|---|---|
| `BrowserApplication.kt` | Initializes `GeckoRuntime` + loads built-in WebExtension |
| `StraitArchitecture.kt` | Core Strait orchestrator |
| `MainActivity.kt` | Compose entrypoint, WebAuthn, download receiver |
| `BrowserViewModel.kt` | ~1700 lines — main state coordinator for UI |

### Documentation reference

Existing docs: `DOCUMENTATION.md` (technical deep-dive), `JusBrowse-Strait-Project-Specification.txt` (the single source of truth spec), `INSTALLATION.md`, `FAQ.md`. If docs conflict with config or scripts, trust the executable source.

## Skill-Driven Execution (OpenCode)

This repo bundles 23 project-local skills under `.opencode/skills/` and references the `agent-skills/` plugin. When a task matches, load the skill — never implement directly.

- Feature → `spec-driven-development` → `incremental-implementation` → `test-driven-development`
- Bug → `debugging-and-error-recovery`
- Code review → `code-review-and-quality`
- Refactoring → `code-simplification`
- UI work → `frontend-ui-engineering`
- Planning → `planning-and-task-breakdown`
- API/interface → `api-and-interface-design`

**Anti-rationalization**: "too small for a skill", "I'll gather context first" — ignore these. Check skills first.
