package models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime


object Users : IntIdTable() {
    val email: Column<String> = varchar("name", length = 50).uniqueIndex()
    val firstName: Column<String> = varchar("first_name", length = 255)
    val lastName: Column<String> = varchar("last_name", length = 255)
    val password: Column<String> = varchar("password", length = 100)
    val isActive: Column<Boolean> = bool("is_active").default(false)
    val created: Column<LocalDateTime> = datetime("created").default(LocalDateTime.now())
    val modified: Column<LocalDateTime> = datetime("modified").default(LocalDateTime.now())
    val currentMeeting = optReference("current_meeting", Meetings)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    val email by Users.email
    val firstName by Users.firstName
    val lastName by Users.lastName
    val password by Users.password
    val isActive by Users.isActive
    val created by Users.created
    val modified by Users.modified
    val currentMeeting by Meeting optionalReferencedOn Users.currentMeeting

}
