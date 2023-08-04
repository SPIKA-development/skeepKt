package network

import io.ktor.client.call.*
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID

@Serializable
data class ViewedPlayer(val username: String)

