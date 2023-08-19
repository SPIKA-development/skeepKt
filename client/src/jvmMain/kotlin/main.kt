import io.ktor.client.engine.cio.*
import java.util.*

class Main

suspend fun main() {
    val clientProps = Properties().apply {
        load(Main::class.java.getResourceAsStream("client.properties"))
    }
    currentUrl = clientProps["server"]!!.toString()
    version = clientProps["version"]!!.toString()
    engine = CIO
    startMain()
}