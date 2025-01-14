package daos

import dtos.*
import generateTokens
import models.Users
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

fun loginUser(data: SigninDTO): Pair<Response, String?> {
    try {
        val user = transaction {
            Users.selectAll().where { Users.email eq data.email }.withDistinct().firstOrNull()
        }

        if (user == null) {
            return Pair(Response.GenericResponse(status = "error", message = "User does not exist!"), null)
        }

        val pwdIsValid = BCrypt.checkpw(data.password, user[Users.password])
        if (!pwdIsValid) {
            return Pair(Response.GenericResponse(status = "error", message = "Invalid email or password!"), null)
        }

        val userClaim = mapOf("id" to user[Users.id], "email" to user[Users.email], "isActive" to user[Users.isActive])
        val token = generateTokens(userClaim)

        return Pair(
            Response.AuthResponse(
                status = "success",
                message = "User login successful",
                data = UserDTO(
                    accessToken = token.first,
                    id = user[Users.id],
                    email = user[Users.email],
                    firstName = user[Users.firstName],
                    lastName = user[Users.lastName],
                    isActive = user[Users.isActive],
                    created = user[Users.created].toString(),
                    modified = user[Users.modified].toString()

                )
            ), token.first
        )
    } catch (e: ExposedSQLException) {
        return Pair(Response.GenericResponse(status = "error", message = "DB error: $e"), null)
    }
}

fun createUser(data: SignupDTO): Pair<Response, String?> {
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
            return Pair(Response.GenericResponse(status = "error", message = "User does not exist!"), null)
        }

        val userClaim = mapOf("id" to user[Users.id], "email" to user[Users.email], "isActive" to user[Users.isActive])
        val token = generateTokens(userClaim)

        return Pair(
            Response.AuthResponse(
                status = "success",
                message = "User creation successful!",
                data = UserDTO(
                    accessToken = token.first,
                    id = user[Users.id],
                    email = user[Users.email],
                    firstName = user[Users.firstName],
                    lastName = user[Users.lastName],
                    isActive = user[Users.isActive],
                    created = LocalDateTime.now().toString(),
                    modified = LocalDateTime.now().toString()
                )
            ), token.first
        )
    } catch (e: ExposedSQLException) {
        return Pair(Response.GenericResponse(status = "error", message = "User already exists!"), null)
    }
}

fun updatePassword(email: String, data: PasswordResetConfirmPayload): Response {
    try {
        transaction {
            Users.update({ Users.email eq email }) {
                it[password] = BCrypt.hashpw(data.newPassword, BCrypt.gensalt())
            }
        }
        return Response.GenericResponse(status = "success", message = "Password resetted successfully!")
    } catch (e: ExposedSQLException) {
        return Response.GenericResponse(status = "error", message = "Failed update: ${e.message}")
    }
}