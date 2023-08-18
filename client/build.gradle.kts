import korlibs.korge.gradle.KorgeGradlePlugin
import korlibs.korge.gradle.Orientation
import korlibs.korge.gradle.korge
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply<KorgeGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
plugins {
    kotlin("multiplatform")
    id("com.android.application")
}

korge {
    id = "io.github.bruce0203.skeep"
    targetJvm()
    targetJs()
    targetDesktopCross()
    targetDesktop()
    targetAndroid()
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
        val jsMain by getting {
            dependencies {
                api(libs.ktor.client.js)
            }
        }
        val jvmMain by getting {
            dependencies {
                api(libs.ktor.client.cio)
                api(libs.logback)
            }
        }
        runCatching {
            val mingwX64Main by getting {
                dependencies {
                    api(libs.ktor.client.winhttp)
                    api(libs.logback)
                }
            }
            val macosArm64Main by getting {
                dependencies {
                    api(libs.ktor.client.cio)
                    api(libs.logback)
                }
            }
            val macosX64Main by getting {
                dependencies {
                    api(libs.ktor.client.cio)
                    api(libs.logback)
                }
            }
        }
    }
    linuxArm64().apply {
        configurations.filter { it.name.contains("linuxArm64") }.forEach {
            it.exclude(libs.kotlinx.uuid.asProvider())
            it.exclude(libs.kotlinx.serialization)
            it.exclude(libs.koin)
            it.exclude(libs.ktor.client.auth)
            it.exclude(libs.ktor.client.content.negotation)
            it.exclude(libs.ktor.serialization.kotlinx.json)

        }
    }
}


fun Configuration.exclude(provider: Provider<MinimalExternalModuleDependency>) {
    val module = provider.get().module
    exclude(group = module.group, module = module.name)
}