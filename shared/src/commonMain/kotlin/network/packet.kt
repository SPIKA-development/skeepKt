package network

import io.ktor.util.reflect.*
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlin.reflect.KClass

interface PacketController<T : Any> {
    val typeInfo: TypeInfo
    fun invoke(t: T): Any
}

inline fun <reified T : Any> packet(clazz: KClass<T> = T::class, crossinline code: (T) -> Any) = object : PacketController<T> {
    override fun invoke(t: T) = code(t)
    override val typeInfo: TypeInfo = typeInfo<T>()
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
