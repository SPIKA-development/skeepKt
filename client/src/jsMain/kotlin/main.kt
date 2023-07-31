import io.ktor.client.engine.js.*
import network.ClientEngineFactory
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

suspend fun main() {
    startKoin {}
    KoinPlatform.getKoin().loadModules(listOf(module {
        factory {
            object : ClientEngineFactory { override fun getEngine() = Js }
        } bind ClientEngineFactory::class
    }))
    start()
}