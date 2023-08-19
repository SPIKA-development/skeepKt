import korlibs.korge.gradle.KorgeGradlePlugin
import korlibs.korge.gradle.KorgeLibraryGradlePlugin
import korlibs.korge.gradle.Orientation
import korlibs.korge.gradle.korge
import korlibs.korge.gradle.typedresources.GenerateTypedResourcesTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

apply<KorgeGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
plugins {
    kotlin("multiplatform")
}

korge {
    targetJvm()
    targetJs()
    targetDesktopCross()
    targetDesktop()
}

tasks.create<Delete>("disableKRes") {
    dependsOn(tasks.withType<GenerateTypedResourcesTask>())
    afterEvaluate {
        val file = File(buildDir, "KR/KR.kt").delete()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kotlin {
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

