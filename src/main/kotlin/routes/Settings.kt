package routes

import dtos.Response
import dtos.SettingsDTO
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.Settings
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.settingsRouting() {
    authenticate {
        route("/v1/settings") {
            get {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userClaim = principal?.payload?.claims?.get("user")

                    if (userClaim == null || userClaim.asMap()["isActive"] == false) {
                        call.respond(
                            status = HttpStatusCode.Unauthorized,
                            Response.GenericResponse(status = "error", message = "Unauthorised user")
                        )
                    }

                    val userId = userClaim!!.asMap()["id"].toString().toInt()

                    val settings = transaction {
                        Settings.selectAll().withDistinct().where { Settings.user eq userId }.firstOrNull()
                    }

                    if (settings == null) {
                        call.respond(
                            status = HttpStatusCode.NotFound,
                            Response.GenericResponse(status = "error", message = "Settings not found")
                        )
                    }

                    call.respond(
                        status = HttpStatusCode.OK,
                        Response.SettingsResponse(
                            status = "success",
                            message = "Settings retrieved successfully",
                            data = SettingsDTO(maxParticipantsDisplayedPerScreenInGalleryView = settings!![Settings.maxParticipantsDisplayedPerScreenInGalleryView])
                        )
                    )
                } catch (e: ExposedSQLException) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        Response.GenericResponse(status = "error", message = "Database error occurred")
                    )
                }

            }
        }
    }
}