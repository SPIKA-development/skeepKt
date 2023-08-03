package network

import io.ktor.serialization.kotlinx.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.uuid.UUID
import kotlin.reflect.KClass

val serialFormat = Json

interface PacketController<T : Any> {
    val typeInfo: TypeInfo
    suspend fun invoke(t: T): Any
}

inline fun <reified T : Any> packet(crossinline code: suspend (T) -> Any) = object : PacketController<T> {
    override suspend fun invoke(t: T) = code(t)
    override val typeInfo: TypeInfo = typeInfo<T>()
}

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
fun decode(data: String, typeInfo: TypeInfo): Any? {
    val serializer = serialFormat.serializersModule.serializerForTypeInfo(typeInfo)
    @Suppress("UNCHECKED_CAST")
    return serialFormat.decodeFromString(serializer as KSerializer<Any?>, data)
}

@Serializable
data class PacketFrame(
    val type: Int,
    val session: UUID,
    val data: String
)

enum class ClientPacket {
    GET_ROOM_NUMBER,
    LEAVE_ROOM,
    CHAT
}

enum class ServerPacket {
    CHAT
}