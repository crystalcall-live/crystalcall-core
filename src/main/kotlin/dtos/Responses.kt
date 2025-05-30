package dtos

import kotlinx.serialization.Serializable

@Serializable
data class MeetingDTO(
    val title: String? = null,
    val username: String,
    val link: String
)

@Serializable
data class SettingsDTO(
    val maxParticipantsDisplayedPerScreenInGalleryView: Int
)

@Serializable
sealed class Response {
    abstract val status: String
    abstract val message: String

    @Serializable
    data class GenericResponse(
        override val status: String,
        override val message: String
    ) : Response()

    @Serializable
    data class AuthResponse(
        override val status: String,
        override val message: String,
        val data: UserDTO
    ) : Response()

    @Serializable
    data class MeetingResponse(
        override val status: String,
        override val message: String,
        val data: MeetingDTO
    ) : Response()

    @Serializable
    data class TokenResponse(
        override val status: String,
        override val message: String,
        val token: String
    ) : Response()

    @Serializable
    data class SettingsResponse(
        override val status: String,
        override val message: String,
        val data: SettingsDTO
    ) : Response()
}