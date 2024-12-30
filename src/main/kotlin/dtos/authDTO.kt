package dtos

import kotlinx.serialization.Serializable

@Serializable
data class AuthDTO(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val status: String,
    val message: String,
    val data: UserDTO? = null
)