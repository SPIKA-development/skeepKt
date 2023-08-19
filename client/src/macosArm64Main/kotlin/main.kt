import korlibs.io.file.std.resourcesVfs
import korlibs.io.lang.readProperties
import kotlinx.coroutines.runBlocking
import io.ktor.client.engine.cio.*

class Main
fun runMain() = main()
fun main() {
    runBlocking {
        val clientProps = resourcesVfs["client.properties"].readProperties()
        currentUrl = clientProps["server"]!!
        version = clientProps["version"]!!
        engine = CIO
        startMain()
    }
}