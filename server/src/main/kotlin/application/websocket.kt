package application

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.uuid.UUID
import model.Room
import model.Rooms
import network.PacketController
import network.PacketFrame
import network.ServerPacket
import network.ServerPacket.GET_ROOM_NUMBER
import network.packet
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.full.createType
import kotlin.reflect.javaType

val serialFormat = Json

fun Application.configureWebsocket() {
    install(WebSockets) {
        val _15sec = Duration.ofSeconds(15)
        pingPeriod = _15sec
        timeout = _15sec
        contentConverter = KotlinxWebsocketSerializationConverter(serialFormat)
    }
    routing {
        webSocket {
            for (frame in incoming) {
                frame.readBytes()
                val packet = receiveDeserialized<PacketFrame>()
                val serverPacketCT = serverPacket(ServerPacket.values()[packet.type])
                serverPacketCT.invoke(serverPacketCT.decode(packet.data)!!)
            }
        }
    }
}

@OptIn(InternalSerializationApi::class, ExperimentalStdlibApi::class, ExperimentalSerializationApi::class)
private fun PacketController<*>.decode(data: String): Any? {
    val type = clazz.createType(clazz.allSupertypes.map { KTypeProjection(null, it) })
    val typeInfo = typeInfoImpl(type.javaType, clazz, type)
    val serializer = serialFormat.serializersModule.serializerForTypeInfo(typeInfo)
    @Suppress("UNCHECKED_CAST")
    return serialFormat.decodeFromString(serializer as KSerializer<Any?>, data)
}

@Suppress("UNCHECKED_CAST")
fun serverPacket(serverPacket: ServerPacket): PacketController<Any> = when(serverPacket) {
    GET_ROOM_NUMBER -> packet<UUID> {
        transaction { Room.find(Rooms.id eq it).first().name }
    }
    ServerPacket.LEAVE_ROOM -> packet {

    }
} as PacketController<Any>

inline fun <reified T : Any> transactionPacket(crossinline code: (T) -> Any) = object : PacketController<T> {
    override fun invoke(t: T) = transaction { code(t) }
    override val clazz: KClass<T> = T::class
}