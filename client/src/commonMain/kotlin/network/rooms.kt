package network

import currentUrl
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.uuid.UUID
import sessionId
import username

suspend fun createRoom(createRoom: CreateRoom) = sendHttp("rooms/create", createRoom).body<CreateRoomResult>()
suspend fun getViewedRooms() = runCatching {
    client().post("$currentUrl/rooms") {
        basicAuth(username, sessionId)
    }.body<List<ViewedRoom>>()
}.apply { this.exceptionOrNull()?.printStackTrace() }.getOrElse { listOf() }

suspend fun joinRoom(uuid: UUID) = sendHttp("rooms/join", uuid).status

suspend fun requestLeaveRoom(uuid: UUID) = sendHttp("rooms/leave", uuid).status

suspend fun getRoomName(uuid: UUID) = sendHttp("rooms/name", uuid).body<String>()

suspend fun listPlayer() = sendHttp("rooms/players").body<List<ViewedPlayer>>()
