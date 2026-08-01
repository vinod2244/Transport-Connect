// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// Define Version Constants
const val KOTLIN_VERSION = "1.9.10"
const val GRADLE_VERSION = "8.2.0"
const val MIN_SDK_VERSION = 24
const val TARGET_SDK_VERSION = 34
const val COMPILE_SDK_VERSION = 34
