package daos

import dtos.*
import models.Users
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt
import org.postgresql.util.PSQLException
import java.time.LocalDateTime

fun loginUser(data: AuthDTO, db: Database): AuthResponse{
    try {
        db.useTransaction {
            val s: MutableMap<String, Any> = mutableMapOf()
            val query = db.from(Users).select(Users.columns).where { Users.email eq data.email }.map { row -> Users.createEntity(row) }.firstOrNull()
            println("${query?.password}, ${data.email} $query heh")

            val pwdIsValid = BCrypt.checkpw(data.password, query?.password)
            if (pwdIsValid) {
                return AuthResponse(
                    status = "success",
                    message = "User login successful",
                    data = UserDTO(
                        id = s["id"] as Int,
                        email = s["email"].toString(),
                        isActive = s["isActive"] as Boolean,
                        created = s["created"].toString(),
                        modified = s["modified"].toString()
                    )
                )
            } else {
                return AuthResponse(status = "error", message = "Invalid email or password!")
            }
        }
    } catch (e: PSQLException) {
        return AuthResponse(status = "error", message = "DB error: $e")
    }
}

fun creatUser(data: AuthDTO, db: Database): AuthResponse {
    val hashedPwd = BCrypt.hashpw(data.password, BCrypt.gensalt())

    try {
        val res = db.useTransaction {
            db.insert(Users) {
                set(it.email, data.email)
                set(it.password, hashedPwd)
                set(it.created, LocalDateTime.now())
                set(it.modified, LocalDateTime.now())
            }
        }
        println("$res res")
        return AuthResponse(status = "success", message = "User creation successful!")
    } catch (e: PSQLException) {
        return AuthResponse(status = "error", message = "DB error: $e")
    }
}
