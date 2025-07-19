-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-verbose
-dontpreverify
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively
-keepattributes *Annotation*


####################################################################  KEEP ANDROID SUPPORT V7 AND DESIGN

-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

-keep class androidx.fragment.app.FragmentManagerState { *; }
-keep class androidx.fragment.app.Fragment { *; }

-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-keep interface android.support.v4.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.** { *; }

####################################################################  REMOVE WARNINGS 


-dontwarn android.support.design.internal.**
-dontwarn com.google.android.gms.**
-dontwarn android.support.v4.**


####################################################################  REMOVE LOGGING 

-assumenosideeffects class android.util.Log {
    public static *** e(...);
    public static *** w(...);
    public static *** wtf(...);
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

####################################################################  ORG.APACHE.HTTP 

-keep class org.apache.http.**
-keep interface org.apache.http.**
-dontwarn org.apache.**

####################################################################  WEVVIEW 

-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient
-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

####################################################################  GOOGLE PLAY SERVICES LIB - ADS

-keep public class com.google.android.gms.* { public *; }
#-keep class com.google.android.gms.**

-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}

-keep public class com.google.android.gms.flags.impl.FlagProviderImpl {
  public <fields>; public <methods>;
}




####################################################################  PARCABLE NEEDED FOR REFLECTION

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
####################################################################  Class/MEMBER NAME FOR CLIENT FUNCTIONALITY

-keep @interface com.google.android.gms.common.annotation.KeepName
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}


####################################################################  BUILD AGAINST PRE_MARSHMALLOW SDK
 -dontwarn android.security.NetworkSecurityPolicy
####################################################################  METADATA ABOUT INCLUDE MODULES

-keep public class com.google.android.gms.dynamite.descriptors.** {
  public <fields>;
}

####################################################################  DATABINDING
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }
-keep class com.intsig.**{ *; }
-keep interface com.intsig.**{ *; }
-keep class preview.**{ *; }
-keep interface preview.**{ *;}
-keep class util.**{ *;}
-keep interface util.**{ *;}
####################################################################  RETROFIT CLASSES AND METHODS
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
####################################################################  IGNORE WARNING
-ignorewarnings
####################################################################  FOR CLASSES ALL ACCESS MODE
-keep class * {
public private protected *;
}
####################################################################  SET/GET
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
 ####################################################################  CLASS WITH PARCABLE INTERFACE
 -keep public class * extends android.app.Activity
 -keepclassmembers class * implements android.os.Parcelable {
 static ** CREATOR;
 }

-keep class in.ecomexpress.sathi.repo.remote.model.attendance.** {  public <methods>; }
-keep class in.ecomexpress.sathi.repo.remote.model.base.** { public <methods>; }

-keep, allowobfuscation class in.ecomexpress.sathi.repo.remote.model.attendance.*
-keepclassmembers, allowobfuscation class * {
    *;
}

-keepnames class in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceRequest
-keepclassmembernames class in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceRequest {
    public <methods>;
    public <fields>;
}

# -keep public class packageName.ParticularClassName.** { *; }

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
-keepclassmembers class * {
    private <fields>;
}
-keepclassmembers class * {
    public <fields>;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class com.google.android.gms.* {  *; }
-dontwarn com.google.android.gms.**
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-dontnote **ILicensingService
-dontnote com.google.android.gms.gcm.GcmListenerService
-dontnote com.google.android.gms.**

-dontwarn com.google.android.gms.ads.**
####################################################################  VIEWMODEL
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
    }
    -keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
        <init>(...);
    }
-keep class androidx.appcompat.widget.** { *; }