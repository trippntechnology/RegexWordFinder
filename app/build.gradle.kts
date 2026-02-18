import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.trippntechnology.regexwordfinder"
    compileSdk = AppInfo.AndroidSdk.COMPILE

    defaultConfig {
        applicationId = AppInfo.APPLICATION_ID
        minSdk = AppInfo.AndroidSdk.MIN
        targetSdk = AppInfo.AndroidSdk.TARGET
        versionCode = AppInfo.Version.CODE
        versionName = AppInfo.Version.NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JVM_17)
            optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
            optIn.add("org.koin.core.annotation.KoinExperimentalAPI")
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.startup)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.okio)
    implementation(libs.okio.assetfilesystem)
    testImplementation(libs.assertk)
    testImplementation(libs.junit)
    testImplementation(libs.koin.test)
}
