import korlibs.korge.gradle.KorgeGradlePlugin
import korlibs.korge.gradle.korge
import korlibs.korge.gradle.typedresources.GenerateTypedResourcesTask

apply<KorgeGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
plugins {
    kotlin("multiplatform")
}

korge {
    targetJvm()
}

kotlin {
    sourceSets {
        val commonMain by getting {
            resources.apply {
                setSrcDirs(srcDirs
                    .also { it.add(File(project(":client").projectDir,  "src/commonMain/resources")) })
            }
        }
    }
}

tasks.create<Delete>("disableKRes") {
    dependsOn(tasks.withType<GenerateTypedResourcesTask>())
    afterEvaluate { File(buildDir, "KR/KR.kt").delete() }
}
