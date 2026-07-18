# ==============================
# AGGRESSIVE R8 OPTIMIZATION
# ==============================
# Allow R8 to aggressively optimize
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively
-repackageclasses ''

# ==============================
# GECKOVIEW: native and bridge rules
# ==============================
-keep class org.mozilla.geckoview.** { *; }
-keep interface org.mozilla.geckoview.** { *; }
-keepclassmembers class org.mozilla.geckoview.** {
    @org.mozilla.geckoview.Annotations$K_SNC *;
}
-dontwarn org.mozilla.geckoview.**

# ==============================
# MEDIA3 / EXOPLAYER: native/reflection rules
# ==============================
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ==============================
# COIL: image loading
# ==============================
-dontwarn coil.**

# ==============================
# OKHTTP
# ==============================
-dontwarn okhttp3.**
-dontwarn okio.**

# ==============================
# AGGRESSIVE STRIPPING: assumenosideeffects
# ==============================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}

# Strip all logging
-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}

# ==============================
# KEEP: general framework items
# ==============================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*, Signature, Exception, InnerClasses
-keepattributes EnclosingMethod

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data classes used with Gson
-keep,allowobfuscation class com.jusdots.jusbrowse.data.** { *; }

# Keep Kotlin metadata for Compose
-keep class kotlin.Metadata { *; }

# ==============================
# AGGRESSIVE STRIPPING — additional
# ==============================

# Strip unused native libraries (only keep arm64-v8a + armeabi-v7a per ABI splits)
# Assumenosideeffects for Kotlin Intrinsics to remove null checks in release builds
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
    static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
    static void checkFieldIsNotNull(java.lang.Object, java.lang.String);
}

# Strip Kotlin source debug metadata (already have SourceFile,LineNumberTable)
-assumenosideeffects class kotlin.coroutines.jvm.internal.DebugMetadataKt {
    static *** getDebugMetadata(...);
}

# Strip Compose debug overhead in release
-dontwarn androidx.compose.runtime.internal.**

# Strip unused JSR 305 annotations
-dontwarn javax.annotation.**
-dontwarn javax.annotation.concurrent.**

# Strip unused GeckoView debug logging
-assumenosideeffects class org.mozilla.geckoview.Log {
    public static void v(...);
    public static void d(...);
    public static void i(...);
}