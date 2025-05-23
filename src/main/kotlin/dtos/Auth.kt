package dtos

import kotlinx.serialization.Serializable

@Serializable
data class SigninDTO(
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

@Serializable
data class PasswordResetPayload(
    val email: String
)

@Serializable
data class PasswordResetConfirmPayload(
    val newPassword: String,
    val confirmNewPassword: String
)

@Serializable
data class PasswordResetChangePayload(
    val oldPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)