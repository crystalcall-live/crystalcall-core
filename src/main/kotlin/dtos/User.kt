package dtos

import EntityIDSerialiser
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID

@Serializable
data class UserDTO(
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean,
    val created: String,
    val modified: String,
    @Serializable(with = EntityIDSerialiser::class)
    val id: EntityID<Int>? = null,
)