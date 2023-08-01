package network

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class ViewedRoom(
    val name: String,
    val maxPlayers: Int,
    val curPlayers: Int,
)

suspend fun createRoom() = client.get("$currentUrl/rooms/create").body<ViewedRoom>()
suspend fun getViewedRooms() = client.get("$currentUrl/rooms").body<List<ViewedRoom>>()
