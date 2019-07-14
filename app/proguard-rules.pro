# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn org.apache.http.**
-dontwarn android.graphics.drawable.**
-dontwarn org.eclipse.jetty.**
-dontwarn org.fourthline.cling.**
-dontwarn org.seamless.**
-dontwarn com.androidquery.**
-dontwarn com.r0adkll.**
-dontwarn com.umeng.update.**
-dontwarn org.jacoco.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn org.xmlpull.v1.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.AppCompatActivity
-keep public class * extends android.app.BaseActionBarActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class * extends android.view.view
-keep class org.xmlpull.v1.** { *; }
-keep class com.baidu.** {  *; }
-keep class org.jsoup.** { *; }
-keep class com.iflytek.** { *; }
-keep class tv.danmaku.ijk.** { *; }
-keep class com.baidu.mobstat.** { *; }
-keep class *.R
-keep class org.apache.commons.httpclient.** { *; }
-keep class com.drovik.player.weather.** { *; }
-keep public class com.iflytek.voiceads.** { *; }
-keep public class com.androidquery.** {  *; }
-keep public class com.drovik.player.news.** { *; }
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#-libraryjars libs/Ad_Android_SDK.jar
#-libraryjars libs/android-query-full.0.26.7.jar
#-libraryjars libs/Baidu_CyberPlayer_SDK_1.7s.jar
#-libraryjars libs/BaiduLBS_Android.jar
#-libraryjars libs/sdk_HeWeather_Public_Android_V2.0.jar
-libraryjars  ../app/libs/armeabi/liblocSDK7b.so
-libraryjars  ../app/libs/armeabi/libindoor.so
#-libraryjars  ../app/src/main/assets/AdDex.4.0.1.dex
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
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keep class vi.com.gdi.bgl.** { *; }
-keep class interfaces.heweather.com.interfacesmodule.** { *;}
-keep class u.upd.** { *; }
-keep class com.alimama.mobile.** { *; }
-keep class com.umeng.update.** { *; }
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-dontpreverify