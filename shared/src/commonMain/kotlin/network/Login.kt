package network

import io.ktor.client.call.*
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import network.LoginResultType.SERVER_IS_NOT_AVAILABLE
import network.LoginResultType.SUCCESS

enum class LoginResultType {
    SUCCESS,
    ALREADY_JOINED,
    SERVER_IS_NOT_AVAILABLE
}

@Serializable
data class LoginResult(
    val result: LoginResultType,
    val uuid: UUID? = null,
)

