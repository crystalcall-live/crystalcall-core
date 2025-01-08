package routes

import Config
import daos.createUser
import daos.loginUser
import daos.updatePassword
import dtos.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.json.Json
import models.Users
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import refreshAccessToken
import sendEmail
import java.time.Instant


fun Route.authRouting() {
    route("/v1/signin") {
        post {
            try {
                val data = call.receive<SigninDTO>()

                val response = loginUser(data)

                if (response.first.status == "success") {
                    call.response.cookies.append(
                        Cookie(
                            name = "refreshToken",
                            value = response.second.toString(),
                            httpOnly = true,
                            secure = true,
                            path = "/v1/tokens/refresh",
                            maxAge = 259200,
                            expires = Instant.now().plusSeconds(259200).toGMTDate()
                        )
                    )

                    call.respond(status = HttpStatusCode.OK, response.first)
                } else if (response.first.message.contains("email")) {
                    call.respond(status = HttpStatusCode.UnprocessableEntity, response.first)
                } else {
                    call.respond(status = HttpStatusCode.NotFound, response.first)
                }
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    Response.GenericResponse(status = "error", message = "Invalid request: ${e.message}")
                )
            }
        }
    }
    route("/v1/signup") {
        post {
            try {
                val data = call.receive<SignupDTO>()

                val response = createUser(data)

                if (response.first.status == "success") {
                    call.response.cookies.append(
                        Cookie(
                            name = "refreshToken",
                            value = response.second.toString(),
                            httpOnly = true,
                            secure = true,
                            path = "/v1/tokens/refresh",
                            maxAge = 259200,
                            expires = Instant.now().plusSeconds(259200).toGMTDate()
                        )
                    )
                    call.respond(status = HttpStatusCode.Created, response.first)
                } else {
                    call.respond(status = HttpStatusCode.NotFound, response.first)
                }
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    Response.GenericResponse(status = "error", message = "Invalid request: ${e.message}")
                )
            }
        }
    }
    route("/v1/tokens/refresh") {
        get {
            try {
                val refreshToken = call.response.cookies["refreshToken"]
                if (refreshToken != null) {
                    call.respond(
                        status = HttpStatusCode.OK,
                        Response.GenericResponse(status = "error", message = "Invalid or expired token")
                    )
                }
                val response = refreshAccessToken(refreshToken.toString())
                call.respond(status = HttpStatusCode.OK, response)
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    Response.GenericResponse(status = "error", message = "Invalid request: ${e.message}")
                )
            }

        }
    }
    route("/v1/oauth/google-callback") {
        post {
            try {
                val client = HttpClient(CIO)
                val data = call.receive<AuthorisationPayload>()

                val response: HttpResponse = client.post(Config.TOKEN_ENDPOINT) {
                    method = HttpMethod.Post
                    contentType(ContentType.Application.Json)
                    setBody(
                        AuthorisationPayload(
                            code = data.code,
                            clientId = Config.CLIENT_ID,
                            clientSecret = Config.CLIENT_SECRET,
                            grantType = "authorization_code",
                            redirectUri = Config.REDIRECT_URI
                        )
                    )
                }
                val jsonResponse = Json.decodeFromString<AuthorisationResponse>(response.bodyAsText())

                call.respond(status = HttpStatusCode.OK, jsonResponse)
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    Response.GenericResponse(status = "error", message = "Invalid request: ${e.message}")
                )
            }

        }
    }
    route("/v1/password/reset") {
        post {
            try {
                val data = call.receive<PasswordResetPayload>()

                val user = transaction {
                    Users.selectAll().where { Users.email eq data.email }.withDistinct().firstOrNull()
                }

                if (user == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        Response.GenericResponse(status = "error", message = "Invalid user")
                    )
                }

                val name = "${user!![Users.firstName]} ${user[Users.lastName]}"
                sendEmail(data.email, name)

                call.respond(
                    status = HttpStatusCode.OK,
                    Response.GenericResponse(status = "success", message = "Password reset email sent successfully!")
                )
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    Response.GenericResponse(status = "error", message = "Invalid request: ${e.message}")
                )
            }

        }
    }
    route("/v1/password/reset/confirm") {
        post {
            try {
                val data = call.receive<PasswordResetConfirmPayload>()

                if (call.request.headers["x-pwd-reset"].isNullOrEmpty() || data.newPassword != data.confirmNewPassword) {
                    call.respond(
                        status = HttpStatusCode.UnprocessableEntity,
                        Response.GenericResponse(status = "error", message = "Invalid password or request")
                    )
                }

                val response = updatePassword(call.request.headers["x-pwd-reset"].toString(), data)

                if (response.status == "success") {
                    call.respond(status = HttpStatusCode.OK, response)
                } else {
                    call.respond(status = HttpStatusCode.UnprocessableEntity, response)
                }
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    Response.GenericResponse(status = "error", message = "Invalid request: ${e.message}")
                )
            }
        }
    }
    authenticate {
        route("/v1/password/change") {
            post {
                try {
                    val data = call.receive<PasswordResetChangePayload>()
                    val principal = call.principal<JWTPrincipal>()
                    val userClaim = principal?.payload?.claims?.get("user")
                    val email = userClaim!!.asMap()["email"].toString()

                    val user = transaction {
                        Users.selectAll().where { Users.email eq email }.withDistinct().firstOrNull()
                    }
                    if (user == null) {
                        call.respond(
                            status = HttpStatusCode.NotFound,
                            Response.GenericResponse(status = "error", message = "Invalid user")
                        )
                    }

                    val pwdIsValid = BCrypt.checkpw(data.oldPassword, user!![Users.password])
                    if (!pwdIsValid || data.newPassword != data.confirmNewPassword) {
                        Response.GenericResponse(status = "error", message = "Invalid password!")
                    }

                    call.respond(
                        status = HttpStatusCode.OK,
                        Response.GenericResponse(status = "success", message = "Password changed successfully!")
                    )


                } catch (e: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        Response.GenericResponse(status = "error", message = "Invalid request: ${e.message}")
                    )
                }
            }
        }
    }

}