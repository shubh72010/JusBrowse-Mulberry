# Installation & Setup Guide

This guide will walk you through building JusBrowse from source. JusBrowse is currently in **Alpha**, so building from source is the recommended way to get the latest security updates.

---

## 🛠️ Prerequisites

- **Android Studio**: Flamingo (2022.2.1) or later (Ladybug/2.2.0 recommended for best Jetpack Compose support).
- **Android SDK**: API Level 34+ (compileSdk is 36).
- **NDK (Side-by-side)**: Required for building the GeckoView native components.
- **Gradle**: Java 11 or 17.

---

## 🏗️ Building the Project

### 1. Clone the Repository
```bash
git clone https://github.com/shubh72010/JusBrowse-GeckoView.git
cd JusBrowse-GeckoView
```

### 2. Open in Android Studio
1. Select **File > Open**.
2. Navigate to the `JusBrowse-GeckoView` folder and click **OK**.
3. Let Gradle sync. This may take a few minutes as it downloads GeckoView (approx. 150MB).

### 3. Setup ABI Splits
To keep the APK size manageable, we use ABI splits. Ensure your `build.gradle.kts` has the correct filters for your device:
```kotlin
ndk {
    abiFilters += listOf("arm64-v8a", "armeabi-v7a")
}
```

### 4. Build & Run
- **Emulator**: Use an 'x86_64' or 'arm64' image with Play Store services.
- **Physical Device**: Enable "Developer Options" and "USB Debugging" on your Android device.

Click the **Run** button (green play icon) in the Android Studio toolbar.

---

## 📦 Known Build Issues

### 1. GeckoView Download Failures
GeckoView is hosted on Mozilla's Maven repository. If the sync fails, check your internet connection or ensure `settings.gradle.kts` matches:
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.mozilla.org/maven2/") }
    }
}
```

### 2. Out of Memory (OOM)
Increasing the Gradle heap size can help:
Add `org.gradle.jvmargs=-Xmx4g` to your `gradle.properties`.

---

## 🧪 Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumentation tests (requires emulator/device):
```bash
./gradlew connectedAndroidTest
```

---
*Questions? Open an issue on GitHub.*
