package daos

import dtos.MeetingDTO
import dtos.Response
import models.Meetings
import models.Users
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction

fun createMeeting(email: String, meetingLink: String): Response {
    try {
        transaction {
            val user = Users.select(Users.id).where { Users.email eq email }.first()
            Meetings.insertAndGetId {
                it[link] = meetingLink
                it[owner] = user[Users.id].value
            }
        }

        return Response.MeetingResponse(
            status = "success",
            message = "Meeting scheduled successfully!",
            data = MeetingDTO(link = meetingLink)
        )
    } catch (e: ExposedSQLException) {
        return Response.GenericResponse(status = "error", message = "Database error occurred")
    }

}