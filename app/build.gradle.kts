plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "com.jusdots.jusbrowse"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jusdots.jusbrowse"
        minSdk = 26 // Security: Kills ancient WebView exploits, enables modern APIs
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.6-3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Security: Enable R8 code shrinking/obfuscation
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
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
    
    // Material Icons Extended — TODO: extract to local JusBrowseIcons.kt
    implementation(libs.androidx.material.icons.extended)
    
    // JSON Serialization
    implementation(libs.gson)
    
    // Airlock Media Viewer Dependencies
    implementation("io.coil-kt:coil-compose:2.6.0")             // Image loading for Compose
    implementation("androidx.media3:media3-exoplayer:1.9.1")    // Core ExoPlayer
    implementation("androidx.media3:media3-ui:1.9.1")           // ExoPlayer UI components
    implementation("androidx.media3:media3-common:1.9.1")       // Media3 common functionality
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}