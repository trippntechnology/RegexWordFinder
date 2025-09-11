import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.arturbosch.detekt.plugin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.undercouch.download.plugin)
}

android {
    namespace = "com.trippntechnology.regexwordfinder"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.trippntechnology.regexwordfinder"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.1"

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
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.google.dagger.hilt.android)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.okio)
    implementation(libs.okio.assetfilesystem)
    ksp(libs.google.dagger.hilt.android.compiler)
    kspTest(libs.google.dagger.compiler)
    testImplementation(libs.assertk)
    testImplementation(libs.junit)
}

// ./gradlew detektDebug
// ./gradlew detektBaselineDebug
detekt {
    allRules = true // fail build on any finding
    buildUponDefaultConfig = true // preconfigure defaults
    config.setFrom(files("$projectDir/build/config/detektConfig.yml")) // point to your custom config defining rules to run, overwriting default behavior
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    // ignore ImageVector files
    exclude("**/ui/icons**")

    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
    }
}
tasks {
    withType<io.gitlab.arturbosch.detekt.Detekt> {
// Target version of the generated JVM bytecode. It is used for type resolution.
        this.jvmTarget = "17"
        dependsOn("downloadDetektConfig")
    }
}
tasks.register<de.undercouch.gradle.tasks.download.Download>("downloadDetektConfig") {
    download {
        onlyIf { !file("build/config/detektConfig.yml").exists() }
        src("https://raw.githubusercontent.com/ICSEng/AndroidPublic/main/detekt/detektConfig-20230728.yml")
        dest("build/config/detektConfig.yml")
    }
}
