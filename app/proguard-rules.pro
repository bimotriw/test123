# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/shilpysamaddar/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
# -keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn okio.**
-dontwarn javax.**
-dontwarn org.springframework.**

-dontnote retrofit2.Platform

-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

-keep class com.amazonaws.services.*.* {*;}
-keep class com.amazonaws.*.* {*;}
-keep class com.amazon.*.* {*;}

 # Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**
-dontwarn com.amazonaws.mobile.auth.facebook.FacebookSignInProvider
-dontwarn com.amazonaws.mobile.auth.google.GoogleSignInProvider
-dontwarn com.amazonaws.mobile.auth.userpools.CognitoUserPoolsSignInProvider

-keep class com.firebase.*.* { *; }
-keep class org.apache.*.* { *; }
-keepnames class com.fasterxml.jackson.*.* { *; }
-keepnames class javax.servlet.*.* { *; }
-keepnames class org.ietf.jgss.*.* { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**
-keep class com.shaded.fasterxml.jackson.*.* { *; }

-keep class com.google.firebase.*.* { *;}
#-keepnames class com.google.firebase.* {*;}

-keep class persistence.*.* {
  *;
}

-keep class cz.msebera.android.httpclient.*.* { *; }
-keep class com.loopj.android.http.*.* { *; }

-keepnames class cz.msebera.android.httpclient.*.* { *; }
-keepnames class com.loopj.android.http.*.* { *; }

-keep class com.oustme.oustsdk.request.*.* { *; }
-keepnames class com.oustme.oustsdk.request.*.* { *; }

#-keepclassmembers class com.oustme.oustsdk.*.*{ *; }

-keep class com.android.volley.*.* { *; }
-keepclassmembers,allowshrinking,allowobfuscation class com.android.volley.NetworkDispatcher {
    void processRequest();
}
-keepclassmembers,allowshrinking,allowobfuscation class com.android.volley.CacheDispatcher {
    void processRequest();
}
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.*.* { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapter.*.* { *; }
-keep class * implements com.google.gson.TypeAdapterFactory.*.* { *; }
-keep class * implements com.google.gson.JsonSerializer.*.* { *; }
-keep class * implements com.google.gson.JsonDeserializer.*.* { *; }

# Prevent R8 from leaving Data object members always null
#-keepclassmembers,allowobfuscation class * {
#  @com.google.gson.annotations.SerializedName <fields>;
#}

##---------------End: proguard configuration for Gson  ----------


# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
#-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform


# GoogleApiClient
# Needed to keep generic types and @Key annotations accessed via reflection
#-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
#-keepclassmembers class * {
  #@com.google.api.client.util.Key <fields>;
#}


# Needed by google-http-client-android when linking against an older platform version
-dontwarn com.google.api.client.extensions.android.**
# Needed by google-api-client-android when linking against an older platform version
-dontwarn com.google.api.client.googleapis.extensions.android.**
# Needed by google-play-services when linking against an older platform version
-dontwarn com.google.android.gms.**
# com.google.client.util.IOUtils references java.nio.file.Files when on Java 7+
-dontnote java.nio.file.Files, java.nio.file.Path
# Suppress notes on LicensingServices
-dontnote **.ILicensingService
# Suppress warnings on sun.misc.Unsafe
-dontnote sun.misc.Unsafe
-dontwarn sun.misc.Unsafe

-dontwarn com.google.common.**
-dontwarn com.google.api.client.json.**


#for enum
-keepclassmembers enum *.* { *; }

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keep class android.support.v7.widget.*.* { *; }
-keep class android.support.v4.app.*.* { *; }
-keep interface android.support.v4.app.*.* { *; }
-keep class android.support.v7.app.*.* { *; }
-keep interface android.support.v7.app.*.* { *; }
#-keep interface android.support.v7.widget.SearchView {
#      public <init>(android.content.Context);
#      public <init>(android.content.Context, android.util.AttributeSet);
#   }

-dontwarn javax.**
-dontwarn io.realm.**

-ignorewarnings
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
