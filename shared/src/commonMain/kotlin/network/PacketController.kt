package network

import io.ktor.serialization.kotlinx.*
import io.ktor.util.reflect.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.uuid.UUID

val serialFormat = ProtoBuf
val converter = KotlinxWebsocketSerializationConverter(ProtoBuf)

interface PacketController<T : Any> {
    val typeInfo: TypeInfo
    suspend fun invoke(t: T): Any
}

inline fun <reified T : Any> packet(crossinline code: suspend (T) -> Any) = object : PacketController<T> {
    override suspend fun invoke(t: T) = code(t)
    override val typeInfo: TypeInfo = typeInfo<T>()
}

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
fun decode(data: ByteArray, typeInfo: TypeInfo): Any? {
    val serializer = serialFormat.serializersModule.serializerForTypeInfo(typeInfo)
    @Suppress("UNCHECKED_CAST")
    return serialFormat.decodeFromByteArray(serializer as KSerializer<Any?>, data)
}

@Serializable
data class PacketFrame(
    val type: Int,
    val session: UUID,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PacketFrame

        if (type != other.type) return false
        if (session != other.session) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + session.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

