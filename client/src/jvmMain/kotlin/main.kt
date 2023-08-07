import io.ktor.client.engine.cio.*
import network.ClientEngineFactory
import network.URLProvider
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import java.util.*

class Main
suspend fun main() {
    val clientProps = Properties().apply {
        load(Main::class.java.getResourceAsStream("client.properties"))
    }
    val url = clientProps["server"]!!.toString()
    val version = clientProps["version"]!!.toString()
    startKoin {}
    KoinPlatform.getKoin().loadModules(listOf(module {
        single(named("version")) { version }
        factory {
            object : URLProvider {
                override val url: String get() = url
            }
        } bind URLProvider::class
        factory {
            object : ClientEngineFactory { override fun getEngine() = CIO }
        } bind ClientEngineFactory::class
    }))
    startMain()
}