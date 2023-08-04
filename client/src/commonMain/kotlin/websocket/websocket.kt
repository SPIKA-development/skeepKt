package websocket

import event.ChatEvent
import io.ktor.util.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.charsets.*
import korlibs.io.net.ws.WebSocketClient
import kotlinx.coroutines.Job
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.*
import sceneContainer
import util.launchNow

var _websocketClient: WebSocketClient? = null
suspend fun websocketClient(): WebSocketClient {
    if (_websocketClient === null) {
        _websocketClient = newWebsocketClient()
    }
    return _websocketClient!!
}
suspend fun newWebsocketClient() = WebSocketClient(currentUrl.httpToWs())

@OptIn(InternalAPI::class)
suspend inline fun <reified T> sendToServer(packet: Enum<*>, t: T) {
    val packetFrame = PacketFrame(packet.ordinal, sessionUUID, Json.encodeToString<T>(t))
    websocketClient().send(Json.encodeToString(packetFrame))
}


suspend fun startWebSocket(): Job {
    return launchNow {
        val websocketClient = websocketClient()
        websocketClient.send(Json.encodeToString(sessionUUID))
        websocketClient.onStringMessage {
            val packetFrame = Json.decodeFromString<PacketFrame>(it)
            val serverPacket = ServerPacket.values()[packetFrame.type]
            val packetController = serverPacket(serverPacket)
            launchNow {
                val data = decode(packetFrame.data, packetController.typeInfo)!!
                packetController.invoke(data)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun serverPacket(serverPacket: ServerPacket): PacketController<Any> = when(serverPacket) {
    ServerPacket.CHAT -> packet<Chat> {
        sceneContainer.dispatch(ChatEvent(it))
    }
} as PacketController<Any>
