package converter

import io.ktor.util.reflect.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure

val format = ProtoBuf

@OptIn(InternalSerializationApi::class)
fun <T>  packetFrame(value: T) = run {
    val kClass = value!!::class
    val type = kClass.createType()
    val typeInfo = typeInfoImpl(type.javaType, kClass, type)
    val serializer = format.serializersModule.serializerForTypeInfo(typeInfo)
    val byteArray = format.encodeToByteArray(serializer)
    PacketFrame(type, byteArray)
}

@Serializable
data class PacketFrame(
    val type: KType,
    val value: ByteArray
) {

    @OptIn(InternalSerializationApi::class)
    fun deserialize() = run {
        val typeInfo = typeInfoImpl(type.javaType, type.jvmErasure, type)
        val serializer = format.serializersModule.serializerForTypeInfo(typeInfo)
        format.decodeFromByteArray(serializer, value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PacketFrame

        if (type != other.type) return false
        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}