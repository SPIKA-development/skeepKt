import io.ktor.client.engine.winhttp.*
import korlibs.io.async.runBlockingNoSuspensions
import korlibs.io.file.std.resourcesVfs
import korlibs.io.lang.readProperties
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import network.ClientEngineFactory
import network.URLProvider
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import util.launchNow

class Main

fun runMain() = main()

fun main() {
    launchNow {
        val clientProps = resourcesVfs["client.properties"].readProperties()
        val url = clientProps["server"]!!
        val version = clientProps["version"]!!
        startKoin {}
        KoinPlatform.getKoin().loadModules(listOf(module {
            single(named("version")) { version }
            factory {
                object : URLProvider {
                    override val url: String get() = url
                }
            } bind URLProvider::class
            factory {
                object : ClientEngineFactory { override fun getEngine() = WinHttp }
            } bind ClientEngineFactory::class
        }))
        start()
    }
}