package network

import kotlinx.serialization.Serializable

enum class ClientPacket {
    GET_ROOM_NUMBER,
    CHAT
}

enum class ServerPacket {
    CHAT,
    PLAYER_JOIN, PLAYER_LEAVE,
    SERVER_CLOSED
}

@Serializable
data class ChatPacket(val username: String, val message: String)

@Serializable
data class PlayerJoinPacket(val username: String)

@Serializable
data class PlayerLeavePacket(val username: String)

@Serializable
class ServerClosedPacket