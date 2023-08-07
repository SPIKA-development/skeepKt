import korlibs.korge.gradle.*
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

apply<KorgeGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
plugins {
    kotlin("multiplatform")
}

korge {
    id = "com.sample.clientserver"
    targetJvm()
    targetJs()
    targetDesktopCross()
    targetDesktop()
    entryPoint = "startMain"
}

kotlin {
    mingwX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":shared"))
                api("de.cketti.unicode:kotlin-codepoints-deluxe:0.6.1")
                api(project(":deps"))
            }
        }
        val jsMain by getting
        val jvmMain by getting
        val mingwX64Main by getting
        val macosArm64Main by getting
    }
}


@Suppress("UnstableApiUsage")
tasks.withType<ProcessResources> {
    filesMatching("client.properties") {
        expand(properties)
    }
}
