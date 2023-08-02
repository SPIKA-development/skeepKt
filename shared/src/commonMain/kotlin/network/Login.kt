package network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import org.koin.mp.KoinPlatform.getKoin

interface URLProvider { val url: String }
val currentUrl get() = getKoin().get<URLProvider>().url
val username: String = generateUsername()
lateinit var sessionId: String

private fun generateUsername() = UUID.generateUUID().toString().substring(0, 4)

suspend fun HttpClient.login() {
    println("Logging in to $currentUrl")
    val response = post("$currentUrl/login") {
        contentType(ContentType.Application.Json)
        setBody(LoginRequest(username))
    }
    sessionId = response.body<UUID>().toString()
}