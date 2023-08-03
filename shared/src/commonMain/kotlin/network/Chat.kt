package network

import kotlinx.serialization.Serializable

@Serializable
data class Chat(val username: String, val message: String)