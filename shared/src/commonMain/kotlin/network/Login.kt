package network

import io.github.bruce0203.skeep.model.LoginRequest
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

const val currentUrl = "http://127.0.0.1:8080"
private fun generateUsername() = UUID.generateUUID().toString().substring(0, 4)

suspend fun login() {
    client.get("$currentUrl/login") {
        contentType(ContentType.Application.Json)
        setBody(LoginRequest(generateUsername()))
    }
}