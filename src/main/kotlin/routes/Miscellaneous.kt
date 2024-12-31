package routes

import Config
import dtos.AuthorisationPayload
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*

fun Route.miscellaneousRouting() {
    route("/v1/google-callback") {
        post {
            val client = HttpClient(CIO)
            val authCode = call.queryParameters["code"]!!

            val response: HttpResponse = client.post(Config.TOKEN_ENDPOINT) {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                setBody(
                    AuthorisationPayload(
                        code = authCode,
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