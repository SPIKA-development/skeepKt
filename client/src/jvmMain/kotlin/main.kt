import io.ktor.client.engine.cio.*
import network.ClientEngineFactory
import network.URLProvider
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import java.io.File
import java.util.Properties

suspend fun main() {
    val url = Properties().apply { load(File("client.properties").inputStream()) }
        .getOrDefault("server", "http://localhost:8080").toString()
    startKoin {}
    KoinPlatform.getKoin().loadModules(listOf(module {
        factory {
            object : URLProvider {
                override val url: String get() = url
            }
        } bind URLProvider::class
        factory {
            object : ClientEngineFactory { override fun getEngine() = CIO }
        } bind ClientEngineFactory::class
    }))
    start()
}