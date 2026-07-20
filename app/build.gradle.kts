plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
    id("kotlin-parcelize")
}

android {
    namespace = "com.jusdots.jusbrowse"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.jusdots.jusbrowse"
        minSdk = 28 // Spec requirement: Android 9.0 (API 28) minimum per JusBrowse-Strait-Project-Specification.txt
        targetSdk = 37
        versionCode = 2

        versionName = "0.0.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        androidResources {
            localeFilters += setOf("en")
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    lint {
        checkReleaseBuilds = false
        checkDependencies = false
        abortOnError = false
        disable += setOf("UnsafeOptInUsageError", "UnsafeOptInUsageWarning")
    }

    packaging {
        resources.excludes += "/assets/dexopt/baseline.prof"
        resources.excludes += "/assets/dexopt/baseline.profm"
        resources.excludes += "META-INF/androidx.profileinstaller_profileinstaller.version"
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Security: Enable R8 code shrinking/obfuscation
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a")
            isUniversalApk = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") { }
            }
        }
    }
}

tasks.matching { it.name.startsWith("check") && it.name.endsWith("AarMetadata") }.configureEach {
    enabled = false
}

tasks.matching { it.name.startsWith("lint") }.configureEach {
    enabled = false
}

tasks.matching { it.name == "validateSigningRelease" }.configureEach {
    enabled = false
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // GeckoView — replaces WebView + Cronet + webkit
    implementation(libs.geckoview)
    implementation(libs.okhttp)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Credential Manager — passkey/WebAuthn support for Google Password Manager
    implementation(libs.androidx.credentials.credentials)
    implementation(libs.androidx.credentials.credentials.play.services.auth)
    
    
    // JSON Serialization
    implementation(libs.gson)

    // Protobuf — binary serialization for tab/session snapshots
    implementation("com.google.protobuf:protobuf-java:${libs.versions.protobuf.get()}")

    // Encrypted SharedPreferences for secure key storage
    implementation("androidx.security:security-crypto:1.1.0")
    
    // Airlock Media Viewer Dependencies
    implementation("io.coil-kt:coil-compose:2.7.0")              // Image loading for Compose
    implementation("androidx.media3:media3-exoplayer:1.10.1")   // Core ExoPlayer
    implementation("androidx.media3:media3-ui:1.10.1")          // ExoPlayer UI components
    implementation("androidx.media3:media3-common:1.10.1")      // Media3 common functionality
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}