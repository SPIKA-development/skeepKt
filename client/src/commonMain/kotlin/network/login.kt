import io.ktor.client.call.*
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import network.LoginRequest
import network.LoginResult
import network.LoginResultType
import network.sendHttp

var username: String = generateUsername()
lateinit var sessionId: String
lateinit var sessionUUID: UUID
val usernameRegex = Regex("[ㄱ-ㅎ가-힣a-zA-Z0-9._]")
private fun generateUsername() = UUID.generateUUID().toString().substring(0, 4)

suspend fun login(loginRequest: LoginRequest = LoginRequest(username)): LoginResultType {
    val loginResult = runCatching { sendHttp("login", loginRequest, auth = false) }
        .apply {println(this.exceptionOrNull()?.stackTraceToString())}.getOrNull()?.body<LoginResult>() ?: return LoginResultType.SERVER_IS_NOT_AVAILABLE
    if (loginResult.result == LoginResultType.SUCCESS) {
        sessionUUID = loginResult.uuid!!
        sessionId = sessionUUID.toString()
    }
    return loginResult.result
}