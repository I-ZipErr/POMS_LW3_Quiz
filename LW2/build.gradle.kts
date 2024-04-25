// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        var nav_version = "2.7.7"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}