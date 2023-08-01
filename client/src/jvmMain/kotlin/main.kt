import io.ktor.client.engine.cio.*
import network.ClientEngineFactory
import network.URLProvider
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

suspend fun main() {
    startKoin {}
    KoinPlatform.getKoin().loadModules(listOf(module {
        factory {
            object : URLProvider {
                override val url: String get() = "http://localhost:8080"
            }
        }
        factory {
            object : ClientEngineFactory { override fun getEngine() = CIO }
        } bind ClientEngineFactory::class
    }))
    start()
}