pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
val userHome = System.getProperty("user.home")
val props = gradle.startParameter.projectProperties.toMutableMap()
props["android.injected.signing.store.file"] = "$userHome/.android/debug.keystore"
props["android.injected.signing.store.password"] = "android"
props["android.injected.signing.key.alias"] = "androiddebugkey"
props["android.injected.signing.key.password"] = "android"
gradle.startParameter.projectProperties = props

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven {
            url = uri("https://maven.mozilla.org/maven2/")
        }
        mavenCentral()
    }
}

rootProject.name = "JusBrowse"
include(":app")
 