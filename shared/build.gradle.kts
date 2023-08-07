import korlibs.korge.gradle.*

apply<KorgeLibraryGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
apply(plugin = "org.jetbrains.kotlin.kapt")
plugins { kotlin("multiplatform") }

korge {
    targetJvm()
    targetJs()
    targetDesktop()
    targetDesktopCross()
    targetDefault()
    targetAll()
}

kotlin {
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