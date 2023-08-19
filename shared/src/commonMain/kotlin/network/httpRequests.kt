package network

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID

@Serializable
enum class CreateRoomResultType { CREATED, NOT_ALLOWED_MAX_PLAYERS_AMOUNT, NOT_ALlOWED_NAME }
@Serializable
data class CreateRoomResult(val type: CreateRoomResultType, val room: UUID = UUID())
