package dtos

import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val email: String,
    val password: String
)

@Serializable
data class SignupDTO(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class AuthResponse(
    val status: String,
    val message: String,
    val data: UserDTO? = null
)

@Serializable
data class AuthorisationPayload(
    val code: String,
    val clientId: String? = null,
    val clientSecret: String? = null,
    val redirectUri: String? = null,
    val grantType: String? = null
)