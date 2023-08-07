import korlibs.korge.gradle.*

apply<KorgeLibraryGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
apply(plugin = "org.jetbrains.kotlin.kapt")
plugins { kotlin("multiplatform") }

korge {
    targetJvm()
    targetJs()
    targetDesktopCross()
}

kotlin {
    targets {
        this
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
        val linuxArm64Main by getting {
            configurations.all {
                println(this.name)
                exclude(libs.kotlinx.uuid.asProvider())
                exclude(libs.kotlinx.serialization)
                exclude(libs.koin)
                exclude(libs.ktor.client.auth)
                exclude(libs.ktor.client.content.negotation)
                exclude(libs.ktor.serialization.kotlinx.json)
            }
        }
    }
}

fun Configuration.exclude(provider: Provider<MinimalExternalModuleDependency>) {
    val module = provider.get().module
    exclude(group = module.group, module = module.name)
}