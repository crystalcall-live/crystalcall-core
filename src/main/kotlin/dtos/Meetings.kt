package dtos

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleMeeting(val title: String? = null, val username: String)