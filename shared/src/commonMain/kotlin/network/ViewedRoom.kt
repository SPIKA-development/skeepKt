package network

import io.ktor.client.call.*
import io.ktor.client.request.*
import korlibs.time.DateTime
import kotlinx.serialization.Serializable

@Serializable
data class ViewedRoom(
    val name: String,
    val maxPlayers: Int,
    val curPlayers: Int,
)

suspend fun createRoom() = client().post("$currentUrl/rooms/create") {
    basicAuth(username, sessionId)
}.body<ViewedRoom>()
suspend fun getViewedRooms() = runCatching {
    client().post("$currentUrl/rooms") {
        basicAuth(username, sessionId)
    }.body<List<ViewedRoom>>()
}.getOrElse { listOf() }
