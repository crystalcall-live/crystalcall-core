package dtos

import kotlinx.serialization.Serializable


@Serializable
sealed class Response.kt {
    @Serializable
    data class GenericResponse(val status: String, val message: String) : `Response.kt`()

    @Serializable
    data class AuthResponse(val status: String, val message: String, val data: UserDTO) : `Response.kt`()

    @Serializable
    data class MessageResponse(val status: String, val message: String, val data: MessageDTO) : `Response.kt`()
}