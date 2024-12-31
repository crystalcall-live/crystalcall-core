package routes

import Config
import configureDatabase
import daos.createUser
import daos.loginUser
import dtos.AuthDTO
import dtos.AuthorisationPayload
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.authRouting() {
    route("/v1/login") {
        post {
            try {
                val data = call.receive<AuthDTO>()
                val db = call.application.configureDatabase()
                val response = loginUser(db, data)

                if (response.status == "success") {
                    call.respond(status = HttpStatusCode.OK, response)
                } else {
                    call.respond(status = HttpStatusCode.UnprocessableEntity, response)
                }
            } catch (e: BadRequestException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    mapOf("status" to "error", "message" to "Invalid request! $e")
                )
            } catch (e: NoSuchElementException) {
                call.respond(status = HttpStatusCode.NotFound, mapOf("status" to "error", "message" to "Invalid user"))
            }
        }
    }
    route("/v1/signup") {
        post {
            try {
                val data = call.receive<AuthDTO>()
                val db = call.application.configureDatabase()
                val response = createUser(db, data)

                if (response.status == "success") {
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
    route("/v1/google-callback") {
        post {
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

            call.respond(HttpStatusCode.OK, response.body())
        }
    }
}