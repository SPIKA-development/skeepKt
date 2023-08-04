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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import util.launchNow
import java.time.Duration
import java.util.*
import kotlin.collections.LinkedHashSet

val serverUUID = UUID.generateUUID()
val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())
fun getRoomConnections(room: UUID) =
    connections.filter { runCatching { getPlayerBySession(it.session).room?.value?.equals(room) }.getOrNull()?: false }

class Connection(val websocket: DefaultWebSocketSession, val session: UUID)
fun Application.configureWebsocket() {
    install(WebSockets) {
        val fifteenSeconds = Duration.ofSeconds(15)
        pingPeriod = fifteenSeconds
        timeout = fifteenSeconds
        contentConverter = KotlinxWebsocketSerializationConverter(serialFormat)
    }
    routing {
        webSocket {
            val thisConnection = Connection(this, receiveDeserialized<UUID>())
            println("Aasdf")
            connections += thisConnection
            try {
                while (true) {
                    val packet = receiveDeserialized<PacketFrame>()
                    val clientPacket = ClientPacket.values()[packet.type]
                    val packetController = serverPacket(this, packet.session, clientPacket)
                    transaction { Session.find(Sessions.id eq packet.session).first() }
                    packetController.invoke(decode(packet.data, packetController.typeInfo)!!)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                launchNow { leaveRoom(thisConnection.session) }
                connections -= thisConnection
            }
        }
    }
}


@Suppress("UNCHECKED_CAST")
suspend fun serverPacket(websocket: DefaultWebSocketSession, session: UUID, clientPacket: ClientPacket): PacketController<Any> = when(clientPacket) {
    ClientPacket.GET_ROOM_NUMBER -> packet<UUID> {
        transaction { Room.find(Rooms.id eq it).first().name }
    }
    ClientPacket.CHAT -> packet<String> { received ->
        val player = getPlayerBySession(session)
        getRoomConnections(player.room!!.value).forEach {
            it.websocket.sendToClient(ServerPacket.CHAT, ChatPacket(player.name, received))
        }
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