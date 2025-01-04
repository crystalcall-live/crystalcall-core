package routes

import Config
import daos.createUser
import daos.loginUser
import dtos.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.json.Json
import utils.generateTokens
import utils.refreshAccessToken
import java.time.Instant


fun Route.authRouting() {
    route("/v1/login") {
        post {
            try {
                val data = call.receive<LoginDTO>()

                val response = loginUser(data)

                if (response.status == "success") {
                    val token = generateTokens(data.email)
                    call.response.cookies.append(
                        Cookie(
                            name = "refreshToken",
                            value = token.second,
                            httpOnly = true,
                            secure = true,
                            path = "/v1/tokens/refresh",
                            maxAge = 259200,
                            expires = Instant.now().plusSeconds(259200).toGMTDate()
                        )
                    )
                    call.respond(status = HttpStatusCode.OK, response)
                } else {
                    call.respond(status = HttpStatusCode.NotFound, response)
                }
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    mapOf("status" to "error", "message" to "Invalid request! $e")
                )
            }
        }
    }
    route("/v1/signup") {
        post {
            try {
                val data = call.receive<SignupDTO>()

                val response = createUser(data)

                if (response.status == "success") {
                    val token = generateTokens(data.email)
                    call.response.cookies.append(
                        Cookie(
                            name = "refreshToken",
                            value = token.second,
                            httpOnly = true,
                            secure = true,
                            path = "/v1/tokens/refresh",
                            maxAge = 259200,
                            expires = Instant.now().plusSeconds(259200).toGMTDate()
                        )
                    )
                    call.respond(status = HttpStatusCode.Created, response)
                } else {
                    call.respond(status = HttpStatusCode.UnprocessableEntity, response)
                }
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    mapOf("status" to "error", "message" to "Invalid request!")
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
                    mapOf("status" to "error", "message" to "Invalid request!")
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
                    mapOf("status" to "error", "message" to "Invalid request!")
                )
            }

        }
    }
}