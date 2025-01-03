import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dtos.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

val MiddlewarePlugin = createApplicationPlugin(name = "MiddlewarePlugin") {
    onCall { call ->
        val refreshToken = call.request.cookies["refreshToken"]
        val accessToken = call.request.cookies["accessToken"]

        val excludedRoutes = listOf("/", "/v1/login", "/v1/signup", "/documentation.yaml")
        
        if (call.request.uri !in excludedRoutes) {
            if (refreshToken.isNullOrEmpty() || accessToken.isNullOrEmpty()) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    Response.GenericResponse(status = "error", message = "Invalid or expired refresh token!")
                )
            } else {
                val decodedJWT = JWT.require(Algorithm.HMAC256(Config.JWT_SECRET)).withAudience(Config.JWT_AUDIENCE)
                    .withIssuer(Config.JWT_ISSUER).build().verify(accessToken)

                if (decodedJWT.expiresAt == null) {
                    call.respond(status = HttpStatusCode.Unauthorized, message = "Invalid or expired access token!")
                }

                println("${decodedJWT.expiresAt.before(java.util.Date())} what")

                if (decodedJWT.expiresAt.before(java.util.Date())) {
                    call.respond(
                        HttpStatusCode.OK,
                        Response.GenericResponse(status = "expired", message = "Invalid or expired access token!")
                    )
                }
            }
        }
    }
}