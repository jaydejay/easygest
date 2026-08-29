# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep your model classes because they are likely used for serialization (Gson, etc.)
-keep class com.jay.easygest.model.** { *; }

# Gson rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# Argon2Kt rules (JNI)
-keep class com.lambdapioneer.argon2kt.** { *; }
-dontwarn com.lambdapioneer.argon2kt.**

# Google API client rules
-keep class com.google.api.client.** { *; }
-keep interface com.google.api.client.** { *; }
-dontwarn com.google.api.client.**

# Preserve line numbers for stack traces (obfuscated names will still be used)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile