package network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.mp.KoinPlatform
import util.launchNow

val clientEngine get() = KoinPlatform.getKoin().get<ClientEngineFactory>().getEngine()

private var _client: HttpClient? = null

suspend fun client() = run {
    if (_client === null) {
        _client = getNewClient()
    }
    _client!!
}


private suspend fun getNewClient() = run {
    HttpClient(clientEngine) {
        install(ContentNegotiation) {
            json()
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }//stopship ktor client engine Js is not support cookie;;;
    }.also {
        it.login()
    }
}
interface ClientEngineFactory {
    fun getEngine(): HttpClientEngineFactory<HttpClientEngineConfig>
}