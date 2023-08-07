import korlibs.korge.gradle.*

apply<KorgeLibraryGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
apply(plugin = "org.jetbrains.kotlin.kapt")
plugins {
    kotlin("multiplatform")
    kotlin("kapt")
}

korge {
    targetJvm()
    targetJs()
    targetDesktop()
    targetDesktopCross()
    targetDesktop()
    entryPoint = "startMain"
}
kapt { generateStubs = true }
kotlin {
    linuxArm64().apply {
        configurations.filter { it.name.contains("linuxArm64") }.forEach {
            println(it.name)
            it.exclude(libs.kotlinx.uuid.asProvider())
            it.exclude(libs.kotlinx.serialization)
            it.exclude(libs.koin)
            it.exclude(libs.ktor.client.auth)
            it.exclude(libs.ktor.client.content.negotation)
            it.exclude(libs.ktor.serialization.kotlinx.json)

        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.uuid)
                api(libs.kotlinx.serialization)
                api(libs.koin)
                api(libs.ktor.client.auth)
                api(libs.ktor.client.content.negotation)
                api(libs.ktor.serialization.kotlinx.json)
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

fun Configuration.exclude(provider: Provider<MinimalExternalModuleDependency>) {
    val module = provider.get().module
    exclude(group = module.group, module = module.name)
}