package network

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String)

@Serializable
data class CreateRoom(val name: String, val maxPlayers: Int, val roomMode: RoomMode) {
    fun testLength() = name.length in AtLeastRoomNameLength..AtMostRoomNameLength
    fun testMaxPlayers() = maxPlayers in AtLeastRoomPlayers..AtMostRoomPlayers
    companion object {
        const val AtLeastRoomPlayers = 2
        const val AtMostRoomPlayers = 12
        const val AtLeastRoomNameLength = 3
        const val AtMostRoomNameLength = 16
        const val defaultRoomMaxPlayers = 6
        fun defaultRoomName(username: String) = "${username}의 방"
    }
}