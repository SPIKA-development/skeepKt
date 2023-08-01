package network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.mp.KoinPlatform

val clientEngine get() = KoinPlatform.getKoin().get<ClientEngineFactory>().getEngine()

val client = HttpClient(clientEngine) {
    install(ContentNegotiation) {
        json()
    }
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }//stopship ktor client engine Js is not support cookie;;;
}
interface ClientEngineFactory {
    fun getEngine(): HttpClientEngineFactory<HttpClientEngineConfig>
}