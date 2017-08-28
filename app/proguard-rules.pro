# Add project specific ProGuard rules here.
 # By default, the flags in this file are appended to flags specified
 # in E:\Android\sdk/tools/proguard/proguard-android.txt
 # You can edit the include path and order by changing the proguardFiles
 # directive in build.gradle.
 #
 # For more details, see
 #   http://developer.android.com/guide/developing/tools/proguard.html

 # Add any project specific keep options here:

 # If your project uses WebView with JS, uncomment the following
 # and specify the fully qualified class name to the JavaScript interface
 # class:
 #-keepclassmembers class fqcn.of.javascript.interface.for.webview {
 #   public *;
 #}

 -dontwarn
 -optimizationpasses 5
 -dontusemixedcaseclassnames
 -dontskipnonpubliclibraryclasses
 -dontpreverify
 -verbose
 -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

 -keep public class * extends Android.app.Activity
 -keep public class * extends android.app.Application
 -keep public class * extends android.app.Service
 -keep public class * extends android.content.BroadcastReceiver
 -keep public class * extends android.content.ContentProvider
 -keep public class * extends android.app.backup.BackupAgentHelper
 -keep public class * extends android.preference.Preference
 -keep public class com.android.vending.licensing.ILicensingService

 -keepclasseswithmembernames class * {
     native <methods>;
 }

 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet);
 }

 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet, int);
 }

 -keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
 }

 -keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
 }

 -keep class * implements android.os.Parcelable {
   public static final android.os.Parcelable$Creator *;
 }

 #友盟
 -keepclassmembers class * {
    public <init> (org.json.JSONObject);
 }

 -keep public class [com.dfqm.web.webdemo].R$*{
 public static final int *;
 }

 -keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
 }

 #okhttputils
 -dontwarn com.zhy.http.**
 -keep class com.zhy.http.**{*;}

 #okhttp
 -dontwarn okhttp3.**
 -keep class okhttp3.**{*;}

 #okio
 -dontwarn okio.**
 -keep class okio.**{*;}

#不提示warnning
-dontwarn [class_filter]

-dontwarn com.tencent.**

-keep class com.dfqm.web.webdemo.entity.** { *; }
#xutil3
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }
-keep public class * extends com.lidroid.xutils.**
-keep public interface org.xutils.** {*;}
-dontwarn org.xutils.**

#PLDroidplayer
-ignorewarnings
-keep class com.pili.pldroid.player.** { *; }
-keep class tv.danmaku.ijk.media.player.** {*;}
-dontwarn com.pili.pldroid.player.**
-dontwarn tv.danmaku.ijk.media.player.**

#eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int d(...);
    public static int w(...);
    public static int v(...);
    public static int i(...);
}




