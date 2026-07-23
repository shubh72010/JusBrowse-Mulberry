# Build & Setup Guide

## Prerequisites

- **Android Studio** Ladybug (2024.3.1) or later (Quail recommended for AGP 9.3 compatibility)
- **Android SDK**: API level 37
- **NDK**: Required for GeckoView native components
- **JDK**: 17

## Build

```bash
git clone <repo-url>
cd JusBrowse-Strait
./gradlew assembleDebug
```

First sync downloads GeckoView Nightly ~150MB from `https://maven.mozilla.org/maven2/`.

## Known Issues

- **OOM**: If Gradle runs out of memory, use `--no-daemon` or increase `org.gradle.jvmargs` in `gradle.properties` (currently `-Xmx3g`).
- **GeckoView Maven**: Ensure `maven.mozilla.org/maven2/` is in your repository list in `settings.gradle.kts`.

## Tests

```bash
./gradlew testDebugUnitTest      # 4 JUnit 4 unit tests
```
