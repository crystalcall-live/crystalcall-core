package routes

import dtos.MeetingDTO
import dtos.Response
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import utils.generateMeetingLink

fun Route.meetingsRouting() {
    authenticate {
        route("/v1/meetings/schedule") {
            post {
                val meetingLink = generateMeetingLink()
                call.respond(
                    status = HttpStatusCode.OK,
                    Response.MeetingResponse(
                        status = "success",
                        message = "Meeting scheduled successfully!",
                        data = MeetingDTO(link = meetingLink)
                    )
                )
            }
        }
    }

}