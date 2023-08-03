package network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.charsets.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import korlibs.io.lang.UTF8
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.encodeToString
import org.koin.mp.KoinPlatform

val currentUrl get() = KoinPlatform.getKoin().get<URLProvider>().url
val clientEngine get() = KoinPlatform.getKoin().get<ClientEngineFactory>().getEngine()

private var clientInst: HttpClient? = null
private var semaphore = false
lateinit var websocket: WebSocketSession
interface URLProvider { val url: String }
interface ClientEngineFactory { fun getEngine(): HttpClientEngineFactory<HttpClientEngineConfig> }

val converter = KotlinxWebsocketSerializationConverter(Json)

@OptIn(InternalAPI::class)
suspend inline fun <reified T> send(clientPacket: ClientPacket, t: T) {
    val packetFrame = PacketFrame(clientPacket.ordinal, sessionUUID, Json.encodeToString<T>(t))
    websocket.sendSerializedBase(packetFrame, typeInfo<PacketFrame>(), converter, Charsets.UTF_8)
}

suspend inline fun <reified T> sendHttp(path: String, body: T, auth: Boolean = true) =
    client().post("$currentUrl/$path") {
        if (auth) basicAuth(username, sessionId)
        if (body !== null) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

suspend inline fun sendHttp(path: String, auth: Boolean = true) =
    client().post("$currentUrl/$path") {
        if (auth) basicAuth(username, sessionId)
    }

@OptIn(InternalCoroutinesApi::class)
suspend fun client(): HttpClient = run {
    if (clientInst == null) {
        println(clientInst == null)
        initializeClient()
    }
    clientInst!!
}


private suspend fun initializeClient() = run {
    clientInst = HttpClient(clientEngine) {
        install(io.ktor.client.plugins.websocket.WebSockets) {
            contentConverter = converter
        }
        install(ContentNegotiation) {
            json()
        }
    }
    clientInst!!.also {
        login()
        websocket = it.webSocketSession(currentUrl.httpToWs())
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