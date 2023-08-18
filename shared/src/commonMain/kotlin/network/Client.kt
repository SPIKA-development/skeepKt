package network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.protobuf.*
import io.ktor.websocket.*
import kotlinx.serialization.protobuf.ProtoBuf
import org.koin.mp.KoinPlatform

val currentUrl get() = KoinPlatform.getKoin().get<URLProvider>().url
val clientEngine get() = KoinPlatform.getKoin().get<ClientEngineFactory>().getEngine()

private var clientInst: HttpClient? = null
suspend fun websocket(): WebSocketSession = client().webSocketSession(currentUrl.httpToWs())
interface URLProvider { val url: String }
interface ClientEngineFactory { fun getEngine(): HttpClientEngineFactory<HttpClientEngineConfig> }

val converter = KotlinxWebsocketSerializationConverter(ProtoBuf)

suspend inline fun <reified T> sendHttp(path: String, body: T, auth: Boolean = true) =
    client().post("$currentUrl/$path") {
        if (auth) basicAuth(username, sessionId)
        if (body !== null) {
            contentType(ContentType.Application.ProtoBuf)
            setBody(body)
        }
    }

suspend inline fun sendHttp(path: String, auth: Boolean = true) =
    client().post("$currentUrl/$path") {
        if (auth) basicAuth(username, sessionId)
    }

suspend fun client(): HttpClient = run {
    if (clientInst == null) {
        initializeClient()
    }
    clientInst!!
}


suspend fun initializeClient() = run {
    clientInst = HttpClient(clientEngine) {
        install(io.ktor.client.plugins.websocket.WebSockets) {
            contentConverter = converter
        }
        install(ContentNegotiation) {
            protobuf()
        }
    }
}

fun String.httpToWs(): String {
    val https = "https://"
    val http = "http://"
    return when (if (startsWith(https)) true else if (startsWith(http)) false else null) {
        true -> "wss://${currentUrl.substring(https.length)}"
        false -> "ws://${currentUrl.substring(http.length)}"
        null -> "ws://$currentUrl"
    }
}

