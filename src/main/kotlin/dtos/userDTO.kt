package dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val email: String,
    val isActive: Boolean,
    val created: String,
    val modified: String,
    val id: Int
)