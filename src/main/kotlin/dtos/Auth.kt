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
data class AuthorisationPayload(
    val code: String,
    val clientId: String? = null,
    val clientSecret: String? = null,
    val redirectUri: String? = null,
    val grantType: String? = null
)

@Serializable
data class AuthorisationResponse(
    val accessToken: String,
    val expiresIn: Int,
    val tokenType: String,
    val scope: String,
    val refreshToken: String
)

@Serializable
data class TokenDTO(
    val accessToken: String,
    val refreshToken: String,
)