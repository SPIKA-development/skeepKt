package network

import event.PacketEvent
import io.ktor.util.*
import korlibs.io.net.ws.WebSocketClient
import korlibs.korge.ui.uiContainer
import korlibs.time.DateTime
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import network.ServerPacket.*
import MainScene
import currentUrl
import scene
import sessionUUID
import ui.loadingMenu
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
suspend fun newWebsocketClient(): WebSocketClient {
    val before  = DateTime.now()
    println(currentUrl.httpToWs())
    WebSocketClient(currentUrl.httpToWs())
    .also { it.startWebSocket() }.also { println(DateTime.now() - before); return it }
}

@OptIn(InternalAPI::class)
suspend inline fun <reified T> sendToServer(packet: Enum<*>, t: T) {
    val packetFrame = PacketFrame(packet.ordinal, sessionUUID, ProtoBuf.encodeToByteArray<T>(t))
    runCatching { websocketClient().send(ProtoBuf.encodeToByteArray(packetFrame)) }.also {
        if (it.isFailure) {
            it.getOrThrow()
            connectionBroke()
        }
        it.getOrThrow()
    }
}

fun connectionBroke() {
    val loading = scene.uiContainer { }
    loading.loadingMenu("서버와의 연결이 예기치 않게 끊겼습니다", "서버 목록으로 돌아가기") {
        loading.removeFromParent()
        launchNow { scene.changeTo<MainScene>() }
    }
}


suspend fun WebSocketClient.startWebSocket() {
    send(ProtoBuf.encodeToByteArray(sessionUUID))
    onBinaryMessage {
        val packetFrame = ProtoBuf.decodeFromByteArray<PacketFrame>(it)
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
    SERVER_CLOSED -> packet<ServerClosedPacket>()
} as PacketController<Any>

private inline fun <reified T : Any> packet() = packet<T> { scene.dispatch(PacketEvent(it)) }
