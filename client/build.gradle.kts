import korlibs.korge.gradle.KorgeGradlePlugin
import korlibs.korge.gradle.Orientation
import korlibs.korge.gradle.korge
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    targetAndroid()
    entryPoint = "runMain"
    orientation = Orientation.DEFAULT
    icon = File(projectDir, "src/commonMain/resources/images/logo.png"
            .replace("/", File.separator))
    exeBaseName = "Skeep"
    name = "Skeep"
    androidManifestChunks.addAll(setOf(
        """<uses-permission android:name="android.permission.INTERNET" />""",
        """<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />"""
    ))
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
//            kotlin.addSrcDir(File(project(":shared").projectDir, "src/commonMain/kotlin"))
            dependencies {
                api("de.cketti.unicode:kotlin-codepoints-deluxe:0.6.1")
                api(project(":deps"))
                api(project(":shared"))
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
        val androidMain by getting {
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

}

configurations.filter { listOf(
    "linuxArm64", "linuxArm64Main", "linuxX64", "linuxX64Main"
).contains(it.name) }.forEach {
    it.exclude(libs.kotlinx.uuid.asProvider())
    it.exclude(libs.kotlinx.serialization)
    it.exclude(libs.ktor.client.auth)
    it.exclude(libs.ktor.client.content.negotation)
    it.exclude(libs.ktor.serialization.kotlinx.json)

}

fun Configuration.exclude(provider: Provider<MinimalExternalModuleDependency>) {
    val module = provider.get().module
    exclude(group = module.group, module = module.name)
}

fun SourceDirectorySet.addSrcDir(file: File) {
    setSrcDirs(srcDirs.apply { add(file) })
}

@Suppress("UnstableApiUsage")
tasks.withType<ProcessResources> {
    filesMatching("client.properties") {
        expand(rootProject.properties)
    }
}
