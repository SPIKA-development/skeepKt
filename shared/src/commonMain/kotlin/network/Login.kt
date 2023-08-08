package network

import io.ktor.client.call.*
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import network.LoginResultType.SERVER_IS_NOT_AVAILABLE
import network.LoginResultType.SUCCESS

var username: String = generateUsername()
lateinit var sessionId: String
lateinit var sessionUUID: UUID
val usernameRegex = Regex("[ㄱ-ㅎ가-힣a-zA-Z0-9._]")
private fun generateUsername() = UUID.generateUUID().toString().substring(0, 4)

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
suspend fun login(loginRequest: LoginRequest = LoginRequest(username)): LoginResultType {
    val loginResult = runCatching { sendHttp("login", loginRequest, auth = false) }
        .getOrNull()?.body<LoginResult>() ?: return SERVER_IS_NOT_AVAILABLE
    if (loginResult.result == SUCCESS) {
        sessionUUID = loginResult.uuid!!
        sessionId = sessionUUID.toString()
    }
    return loginResult.result
}