import io.ktor.client.engine.js.*
import korlibs.io.file.std.resourcesVfs
import korlibs.io.lang.readProperties

suspend fun main() {
    val clientProps = resourcesVfs["client.properties"].readProperties()
    currentUrl = clientProps["server"]!!
    version = clientProps["version"]!!
    engine = Js
    startMain()
}