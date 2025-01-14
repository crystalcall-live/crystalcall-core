package routes

import daos.createMeeting
import dtos.Response
import generateMeetingLink
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.meetingsRouting() {
    authenticate {
        route("/v1/meetings/schedule") {
            post {
                val principal = call.principal<JWTPrincipal>()
                val userClaim = principal?.payload?.claims?.get("user")

                if (userClaim == null || userClaim.asMap()["isActive"] == false) {
                    call.respond(
                        status = HttpStatusCode.Unauthorized,
                        Response.GenericResponse(status = "error", message = "Unauthorised user")
                    )
                }

                val meetingLink = generateMeetingLink()
                val email = userClaim!!.asMap()["email"].toString()

                val response = createMeeting(email, meetingLink)

                if (response.status == "success") {
                    call.respond(status = HttpStatusCode.OK, response)
                } else {
                    call.respond(status = HttpStatusCode.Conflict, response)
                }


            }
        }
    }

}