package websocket

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.uuid.UUID
import network.*

suspend fun createRoom() = client().post("$currentUrl/rooms/create") {
    basicAuth(username, sessionId)
}.body<ViewedRoom>()
suspend fun getViewedRooms() = runCatching {
    client().post("$currentUrl/rooms") {
        basicAuth(username, sessionId)
    }.body<List<ViewedRoom>>()
}.apply { this.exceptionOrNull()?.printStackTrace() }.getOrElse { listOf() }

suspend fun joinRoom(uuid: UUID) = sendHttp("rooms/join", uuid).status

suspend fun leaveRoom(uuid: UUID) = sendHttp("rooms/leave", uuid).status

suspend fun getRoomName(uuid: UUID) = sendHttp("rooms/name", uuid).body<String>()

suspend fun listPlayer() = sendHttp("rooms/players").body<List<ViewedPlayer>>()
