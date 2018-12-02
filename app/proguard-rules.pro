# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/debashispaul/Library/Android/sdk/tools/proguard/proguard-android.txt
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
# some Groovy classes don’t get removed by ProGuard.
-dontobfuscate

-keep class org.codehaus.groovy.vmplugin.**
-keep class org.codehaus.groovy.runtime.dgm*

-keepclassmembers class org.codehaus.groovy.runtime.dgm* {*;}
-keepclassmembers class ** implements org.codehaus.groovy.runtime.GeneratedClosure {*;}
-keepclassmembers class org.codehaus.groovy.reflection.GroovyClassValue* {*;}
-keepclassmembers class groovyx.example.** {*;}
-keepclassmembers class com.arasthel.swissknife.utils.Finder {*;}

-dontwarn org.codehaus.groovy.**
-dontwarn groovy**
-dontnote org.codehaus.groovy.**
-dontnote groovy**

