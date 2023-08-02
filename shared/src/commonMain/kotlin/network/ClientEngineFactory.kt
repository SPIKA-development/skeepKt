package network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.mp.KoinPlatform
import util.launchNow

val clientEngine get() = KoinPlatform.getKoin().get<ClientEngineFactory>().getEngine()

val client by lazy {
    HttpClient(clientEngine) {
        install(ContentNegotiation) {
            json()
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }//stopship ktor client engine Js is not support cookie;;;
    }.also {
        launchNow {
            login()
        }
    }
}
interface ClientEngineFactory {
    fun getEngine(): HttpClientEngineFactory<HttpClientEngineConfig>
}