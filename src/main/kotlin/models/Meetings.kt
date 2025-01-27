package models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Meetings : IntIdTable() {
    val title: Column<String?> = varchar("title", 255).nullable()
    val link: Column<String> = varchar("link", 200)
    val isExpired: Column<Boolean> = bool("is_expired").default(false)
    val created: Column<LocalDateTime> = datetime("created").default(LocalDateTime.now())
    val modified: Column<LocalDateTime> = datetime("modified").default(LocalDateTime.now())
    val owner = reference("owner", Users, onDelete = ReferenceOption.CASCADE)
}

class Meeting(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Meeting>(Meetings)

    val title by Meetings.title
    val link by Meetings.link
    val isExpired by Meetings.isExpired
    val created by Meetings.created
    val modified by Meetings.modified
    val owner by User referencedOn Meetings.owner
    val participants by User optionalReferrersOn Users.currentMeeting
}