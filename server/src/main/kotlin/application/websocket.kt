package application

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import model.*
import network.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.util.*

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
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(serialFormat)
    }
    routing {
        webSocket {
            val thisConnection = Connection(this, receiveDeserialized<UUID>())
            connections += thisConnection

            try {
                while (true) {
                    val packet = receiveDeserialized<PacketFrame>()
                    val clientPacket = ClientPacket.values()[packet.type]
                    val packetController = serverPacket(this, packet.session, clientPacket)
                    if (connections.none { it.session == packet.session }
                        && runCatching { transaction { Session.find(Sessions.id eq packet.session).first() } }.isFailure)
                        return@webSocket
                    packetController.invoke(decode(packet.data, packetController.typeInfo)!!)
                }
            } catch (_: kotlinx.coroutines.channels.ClosedReceiveChannelException) {
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                runCatching { leaveRoom(thisConnection.session) }
                runCatching { logout(thisConnection.session) }
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
    val packetFrame = PacketFrame(packet.ordinal, serverUUID, ProtoBuf.encodeToByteArray<T>(t))
    sendSerializedBase(packetFrame, typeInfo<PacketFrame>(), converter, Charsets.UTF_8)
}
