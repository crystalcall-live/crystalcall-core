package models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Settings : IntIdTable() {
    val maxParticipantsDisplayedPerScreenInGalleryView =
        integer("max_participants_displayed_per_screen_in_gallery_view").default(9)
    val user = optReference("user", Users, onDelete = ReferenceOption.CASCADE)
}

class Setting(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Setting>(Settings)

    val maxParticipantsDisplayedPerScreenInGalleryView by Settings.maxParticipantsDisplayedPerScreenInGalleryView
    val user by User optionalReferencedOn Settings.user
}