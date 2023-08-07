package websocket

import event.PacketEvent
import io.ktor.util.*
import korlibs.io.async.launchImmediately
import korlibs.io.net.ws.WebSocketClient
import korlibs.korge.ui.uiContainer
import korlibs.time.measureTime
import kotlinx.coroutines.Job
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.*
import network.ServerPacket.*
import sceneContainer
import ui.MainMenuState
import ui.loadingMenu
import ui.mainMenu
import util.launchNow

var _websocketClient: WebSocketClient? = null
private var websocketSemaphore = false
suspend fun websocketClient(): WebSocketClient {
    println("asdf")
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
            connectionBroked()
        }
        it.getOrThrow()
    }
}

fun connectionBroked() {
    sceneContainer.removeChildren()
    val loading = sceneContainer.uiContainer { }
    loading.loadingMenu("서버와의 연결이 예기치 않게 끊겼습니다", "서버 목록으로 돌아가기") {
        loading.removeFromParent()
        launchNow { MainMenuState().mainMenu() }
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
