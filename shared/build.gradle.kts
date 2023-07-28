import korlibs.korge.gradle.*

apply<KorgeLibraryGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
apply(plugin = "org.jetbrains.kotlin.kapt")

korge {
    targetJvm()
    targetJs()
}

dependencies {
    add("commonMainApi", libs.kotlinx.serialization)
}