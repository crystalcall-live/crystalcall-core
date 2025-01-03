package models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Meetings : IntIdTable() {
    val link: Column<String> = varchar("link", 255)
    val isExpired: Column<Boolean> = bool("is_expired").default(false)
    val created: Column<LocalDateTime> = datetime("created").default(LocalDateTime.now())
    val modified: Column<LocalDateTime> = datetime("modified").default(LocalDateTime.now())
    val user = reference("users", Users)
}