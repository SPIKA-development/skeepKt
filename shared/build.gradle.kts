import korlibs.korge.gradle.KorgeGradlePlugin
import korlibs.korge.gradle.KorgeLibraryGradlePlugin
import korlibs.korge.gradle.Orientation
import korlibs.korge.gradle.korge
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

apply<KorgeGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
plugins {
    kotlin("multiplatform")
    id("com.android.application")
}

korge {
    targetJvm()
    targetJs()
    targetDesktopCross()
    targetDesktop()
    targetAndroid()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
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

