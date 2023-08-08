package network

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String)

@Serializable
data class CreateRoom(val name: String, val maxPlayers: Int) {
    fun test() = name.length <= defaultRoomNameLength && maxPlayers <= defaultRoomMaxPlayers
    companion object {
        const val defaultRoomMaxPlayers = 6
        const val defaultRoomNameLength = 8
    }
}