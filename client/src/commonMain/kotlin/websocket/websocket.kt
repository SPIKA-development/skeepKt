package websocket

import event.PacketEvent
import io.ktor.util.*
import korlibs.io.net.ws.WebSocketClient
import kotlinx.coroutines.Job
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.*
import network.ServerPacket.*
import sceneContainer
import util.launchNow

var _websocketClient: WebSocketClient? = null
private var websocketSemaphore = false
suspend fun websocketClient(): WebSocketClient {
    if (_websocketClient === null) {
        if (websocketSemaphore) throw AssertionError("blocked by semaphore")
        websocketSemaphore = true
        try { _websocketClient = newWebsocketClient() }
        finally { websocketSemaphore = false }
    }
    return _websocketClient!!
}
suspend fun newWebsocketClient() =
    WebSocketClient(currentUrl.httpToWs())
    .also { it.startWebSocket() }

@OptIn(InternalAPI::class)
suspend inline fun <reified T> sendToServer(packet: Enum<*>, t: T) {
    val packetFrame = PacketFrame(packet.ordinal, sessionUUID, Json.encodeToString<T>(t))
    runCatching { websocketClient().send(Json.encodeToString(packetFrame)) }.also {
        if (it.isFailure) {

        }
        it.getOrThrow()
    }
}


suspend fun WebSocketClient.startWebSocket() {
    send(Json.encodeToString(sessionUUID))
    onStringMessage {
        val packetFrame = Json.decodeFromString<PacketFrame>(it)
        val serverPacket = ServerPacket.values()[packetFrame.type]
        val packetController = serverPacket(serverPacket)
        launchNow {
            val data = decode(packetFrame.data, packetController.typeInfo)!!
            packetController.invoke(data)
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun serverPacket(serverPacket: ServerPacket): PacketController<Any> = when(serverPacket) {
    CHAT -> packet<ChatPacket>()
    PLAYER_JOIN -> packet<PlayerJoinPacket>()
    PLAYER_LEAVE -> packet<PlayerLeavePacket>()
} as PacketController<Any>

private inline fun <reified T : Any> packet() = packet<T> { sceneContainer.dispatch(PacketEvent(it)) }
