import de.undercouch.gradle.tasks.download.Download
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.detekt.plugin) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.undercouch.download.plugin) apply false
}

allprojects {
    apply(plugin = "dev.detekt")
    apply(plugin = "de.undercouch.download")


    tasks.register<Download>("downloadDetektConfig") {
        onlyIf { !file("$projectDir/build/config/detektConfig.yml").exists() }
        src("https://mobile-cdn.churchofjesuschrist.org/android/build/detekt/v2/detektConfig-latest.yml")
        dest("$projectDir/build/config/detektConfig.yml")
    }

    extensions.configure<DetektExtension>("detekt") {
        allRules = true
        buildUponDefaultConfig = true
        config.setFrom(files("$projectDir/build/config/detektConfig.yml"))
    }

    tasks.withType<Detekt>().configureEach {
        exclude("**/ui/icons**")
        dependsOn("downloadDetektConfig")

        reports {
            html.required.set(true)
        }
    }
}
