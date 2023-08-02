package network

import io.ktor.client.call.*
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

val username: String = generateUsername()
lateinit var sessionId: String

private fun generateUsername() = UUID.generateUUID().toString().substring(0, 4)

suspend fun login() {
    sessionId = sendHttp("login", LoginRequest(username), auth = false).body<UUID>().toString()
}