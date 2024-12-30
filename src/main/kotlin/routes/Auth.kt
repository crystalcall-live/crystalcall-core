package routes

import configureDatabase
import daos.creatUser
import daos.loginUser
import dtos.AuthDTO
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
                val response = loginUser(data, db)

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
                val response = creatUser(data, db)

                if (response.status == "success") {
                    call.respond(status = HttpStatusCode.Created, response)
                } else {
                    call.respond(status = HttpStatusCode.UnprocessableEntity, response)
                }
            } catch (e: BadRequestException) {
                call.respond(status = HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to "Invalid request!"))
            }
        }
    }
}