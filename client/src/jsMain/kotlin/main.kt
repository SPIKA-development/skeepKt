import io.ktor.client.engine.js.*
import korlibs.io.file.std.resourcesVfs
import korlibs.io.lang.readProperties
import network.ClientEngineFactory
import network.URLProvider
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

suspend fun main() {
    val url = resourcesVfs["client.properties"].readProperties()["server"]!!
    startKoin {}
    KoinPlatform.getKoin().loadModules(listOf(module {
        factory {
            object : URLProvider {
                override val url: String get() = url
            }
        } bind URLProvider::class
        factory {
            object : ClientEngineFactory { override fun getEngine() = Js }
        } bind ClientEngineFactory::class
    }))
    start()
}