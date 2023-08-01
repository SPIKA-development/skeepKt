package network

import io.github.bruce0203.skeep.model.LoginRequest
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import org.koin.mp.KoinPlatform.getKoin

interface URLProvider { val url: String }
val currentUrl get() = getKoin().get<URLProvider>().url
private fun generateUsername() = UUID.generateUUID().toString().substring(0, 4)

suspend fun login() {
    client.get("$currentUrl/login") {
        contentType(ContentType.Application.Json)
        setBody(LoginRequest(generateUsername()))
    }
}