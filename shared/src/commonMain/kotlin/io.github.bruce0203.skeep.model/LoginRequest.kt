package io.github.bruce0203.skeep.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String)
