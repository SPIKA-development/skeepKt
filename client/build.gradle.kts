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
    orientation = Orientation.LANDSCAPE
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
    sourceSets {
        val commonMain by getting {
//            kotlin.addSrcDir(File(project(":shared").projectDir, "src/commonMain/kotlin"))
            dependencies {
                api("de.cketti.unicode:kotlin-codepoints-deluxe:0.6.1")
                api(project(":deps"))
                api(project(":shared"))
            }
        }
        val jvmTest by getting {
            dependencies {
                kotlin("test")
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

fun SourceDirectorySet.addSrcDir(file: File) {
    setSrcDirs(srcDirs.apply { add(file) })
}

@Suppress("UnstableApiUsage")
tasks.withType<ProcessResources> {
    filesMatching("client.properties") {
        expand(rootProject.properties)
    }
}
