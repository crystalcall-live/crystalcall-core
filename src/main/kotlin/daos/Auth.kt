package daos

import dtos.LoginDTO
import dtos.Response
import dtos.SignupDTO
import dtos.UserDTO
import models.Users
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

fun loginUser(data: LoginDTO): Response {
    try {
        val user = transaction {
            Users.selectAll().where { Users.email eq data.email }.withDistinct().firstOrNull()
        }

        if (user == null) {
            return Response.GenericResponse(status = "error", message = "User does not exist!")
        }

        val pwdIsValid = BCrypt.checkpw(data.password, user[Users.password])
        if (!pwdIsValid) {
            return Response.GenericResponse(status = "error", message = "Invalid email or password!")
        }

        return Response.AuthResponse(
            status = "success",
            message = "User login successful",
            data = UserDTO(
                id = user[Users.id],
                email = user[Users.email],
                firstName = user[Users.firstName],
                lastName = user[Users.lastName],
                isActive = user[Users.isActive],
                created = user[Users.created].toString(),
                modified = user[Users.modified].toString()
            )
        )
    } catch (e: ExposedSQLException) {
        return Response.GenericResponse(status = "error", message = "DB error: $e")
    }
}

fun createUser(data: SignupDTO): Response {
    try {
        val hashedPwd = BCrypt.hashpw(data.password, BCrypt.gensalt())
        val user = transaction {
            val id = Users.insertAndGetId {
                it[email] = data.email
                it[firstName] = data.firstName
                it[lastName] = data.lastName
                it[password] = hashedPwd
                it[created] = LocalDateTime.now()
                it[modified] = LocalDateTime.now()
            }

            Users.selectAll().where { Users.id eq id }.firstOrNull()
        }

        if (user == null) {
            return Response.GenericResponse(status = "error", message = "User does not exist!")
        }

        return Response.AuthResponse(
            status = "success",
            message = "User creation successful!",
            data = UserDTO(
                id = user[Users.id],
                email = user[Users.email],
                firstName = user[Users.firstName],
                lastName = user[Users.lastName],
                isActive = user[Users.isActive],
                created = LocalDateTime.now().toString(),
                modified = LocalDateTime.now().toString()
            )
        )
    } catch (e: ExposedSQLException) {
        return Response.GenericResponse(status = "error", message = "DB error: $e")
    }
}
