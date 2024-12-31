package daos

import dtos.*
import models.Users
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt
import org.postgresql.util.PSQLException
import java.time.LocalDateTime

fun loginUser(db: Database, data: AuthDTO): AuthResponse{
    try {
        val user = db.useTransaction {
            db.from(Users).select(Users.columns).where { Users.email eq data.email }
                .map { row -> Users.createEntity(row) }.firstOrNull()
        }

        if (user == null) {
            return AuthResponse(status = "error", message = "User does not exist!")
        }

        val pwdIsValid = BCrypt.checkpw(data.password, user.password)
        if (!pwdIsValid) {
            return AuthResponse(status = "error", message = "Invalid email or password!")

        }

        return AuthResponse(
            status = "success",
            message = "User login successful",
            data = UserDTO(
                id = user.id,
                email = user.email,
                isActive = user.isActive,
                created = user.created.toString(),
                modified = user.modified.toString()
            )
        )
    } catch (e: PSQLException) {
        return AuthResponse(status = "error", message = "DB error: $e")
    }
}

fun createUser(db: Database, data: AuthDTO): AuthResponse {
    try {
        val hashedPwd = BCrypt.hashpw(data.password, BCrypt.gensalt())
        db.useTransaction {
            db.insert(Users) {
                set(it.email, data.email)
                set(it.password, hashedPwd)
                set(it.created, LocalDateTime.now())
                set(it.modified, LocalDateTime.now())
            }
        }
        return AuthResponse(status = "success", message = "User creation successful!")
    } catch (e: PSQLException) {
        return AuthResponse(status = "error", message = "DB error: $e")
    }
}
