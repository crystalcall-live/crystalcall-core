package models

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDateTime

interface User : Entity<User> {
    companion object : Entity.Factory<User>()
    val id: Int
    var email: String
    var password: String
    var isActive: Boolean
    var created: LocalDateTime
    var modified: LocalDateTime
}


object Users : Table<User>("t_user") {
    val id = int("id").primaryKey().bindTo { it.id }
    val email = varchar("email").bindTo { it.email }
    val password = varchar("password").bindTo { it.password }
    val isActive = boolean("is_active").bindTo { it.isActive }
    val created = datetime("created").bindTo { it.created }
    val modified = datetime("modified").bindTo { it.modified }
}