import korlibs.korge.gradle.KorgeGradlePlugin
import korlibs.korge.gradle.KorgeLibraryGradlePlugin
import korlibs.korge.gradle.Orientation
import korlibs.korge.gradle.korge
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
plugins {
    kotlin("multiplatform")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.uuid)
                api(libs.kotlinx.serialization)
                api(libs.ktor.client.auth)
                api(libs.ktor.client.content.negotation)
                api(libs.ktor.serialization.kotlinx.protobuf)
            }
        }
    }
}

