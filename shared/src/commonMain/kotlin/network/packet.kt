package network

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlin.reflect.KClass

interface PacketController<T : Any> {
    val clazz: KClass<T>
    fun invoke(t: T): Any
}

inline fun <reified T : Any> packet(clazz: KClass<T> = T::class, crossinline code: (T) -> Any) = object : PacketController<T> {
    override fun invoke(t: T) = code(t)
    override val clazz: KClass<T> = clazz
}


@Serializable
data class PacketFrame(
    val type: Int,
    val session: UUID,
    val data: String
)

enum class ServerPacket {
    GET_ROOM_NUMBER,
    LEAVE_ROOM
}