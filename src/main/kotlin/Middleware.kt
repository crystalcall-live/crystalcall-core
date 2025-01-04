import dtos.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

val MiddlewarePlugin = createApplicationPlugin(name = "MiddlewarePlugin") {
    onCall { call ->
        val refreshToken = call.request.cookies["refreshToken"]

        if (call.request.uri == "/v1/tokens/refresh") {
            if (refreshToken.isNullOrEmpty()) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    Response.GenericResponse(status = "error", message = "Invalid or expired token!")
                )
            }
        }
    }
}