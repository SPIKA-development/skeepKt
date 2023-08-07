package network

import io.ktor.client.call.*
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

var username: String = generateUsername()
lateinit var sessionId: String
lateinit var sessionUUID: UUID
val usernameRegex = Regex("[ㄱ-ㅎ가-힣a-zA-Z0-9._]")
private fun generateUsername() = UUID.generateUUID().toString().substring(0, 4)

suspend fun login() {
    sessionUUID = sendHttp("login", LoginRequest(username), auth = false).body<UUID>()
    sessionId = sessionUUID.toString()
}