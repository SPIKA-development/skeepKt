import korlibs.korge.gradle.*
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

apply<KorgeGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
plugins {
    kotlin("multiplatform")
}

korge {
    id = "io.github.bruce0203.skeep"
    targetJvm()
    targetJs()
    targetDesktopCross()
    targetDesktop()
    entryPoint = "runMain"
    orientation = Orientation.DEFAULT
    icon = File(projectDir, "src/commonMain/resources/images/logo.png"
            .replace("/", File.separator))
    exeBaseName = "Skeep"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":shared"))
                api("de.cketti.unicode:kotlin-codepoints-deluxe:0.6.1")
                api(project(":deps"))
            }
        }
        runCatching { val jvmMain by getting }
        runCatching { val mingwX64Main by getting }
        runCatching { val macosArm64Main by getting }
    }
}


@Suppress("UnstableApiUsage")
tasks.withType<ProcessResources> {
    filesMatching("client.properties") {
        expand(rootProject.properties)
    }
}

