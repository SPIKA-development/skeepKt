package application

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import model.*
import network.*
import network.ClientPacket.CHAT
import network.ClientPacket.GET_ROOM_NUMBER
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration

val serverUUID = UUID.generateUUID()

fun Application.configureWebsocket() {
    install(WebSockets) {
        val _15sec = Duration.ofSeconds(15)
        pingPeriod = _15sec
        timeout = _15sec
        contentConverter = KotlinxWebsocketSerializationConverter(serialFormat)
    }
    routing {
        webSocket {
            while (true) {
                val packet = receiveDeserialized<PacketFrame>()
                val clientPacket = ClientPacket.values()[packet.type]
                val packetController = serverPacket(this, packet.session, clientPacket)
                transaction { Session.find(Sessions.id eq packet.session).first() }
                packetController.invoke(decode(packet.data, packetController.typeInfo)!!)
            }
        }
    }
}


@Suppress("UNCHECKED_CAST")
suspend fun serverPacket(websocket: DefaultWebSocketSession, uuid: UUID, clientPacket: ClientPacket): PacketController<Any> = when(clientPacket) {
    GET_ROOM_NUMBER -> packet<UUID> {
        transaction { Room.find(Rooms.id eq it).first().name }
    }
    ClientPacket.LEAVE_ROOM -> packet {

    }
    CHAT -> packet<String> {
        val player = transaction { Player.find(Players.id eq getPlayerBySession(uuid)).first().name }
        websocket.sendToClient(ServerPacket.CHAT, Chat(player, it))
    }
} as PacketController<Any>

inline fun <reified T : Any> transactionPacket(crossinline code: (T) -> Any) = object : PacketController<T> {
    override suspend fun invoke(t: T): Any = transaction { code(t) }
    override val typeInfo: TypeInfo = typeInfo<T>()
}


@OptIn(InternalAPI::class)
suspend inline fun <reified T> DefaultWebSocketSession.sendToClient(packet: Enum<*>, t: T) {
    val packetFrame = PacketFrame(packet.ordinal, serverUUID, Json.encodeToString<T>(t))
    sendSerializedBase(packetFrame, typeInfo<PacketFrame>(), converter, Charsets.UTF_8)
}