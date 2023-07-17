
import com.android.build.gradle.internal.res.processResources
import korlibs.korge.gradle.*

apply<KorgeGradlePlugin>()

korge {
    id = "com.sample.clientserver"
    targetJvm()
    targetJs()
    support3d()
    supportExperimental3d()
}

dependencies {
    add("commonMainImplementation", project(":shared"))
}

dependencies {
    add("commonMainApi", project(":deps"))
}

@Suppress("UnstableApiUsage")
tasks.withType<ProcessResources> {
    filesMatching("client.properties") {
        expand("version" to "1.0.0")
    }
}
