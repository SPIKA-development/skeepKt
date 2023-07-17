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
    add("commonMainApi", "io.insert-koin:koin-core:3.4.2")
}
