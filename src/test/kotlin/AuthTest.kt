import dtos.LoginDTO
import dtos.Response
import dtos.SignupDTO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthTest {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun testLogin() = testApplication {
        application {
            module(testing = true)
        }

        val response = client.post("/v1/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(Json.encodeToString(LoginDTO.serializer(), LoginDTO("test@email.com", "password")))
        }
        println(response.bodyAsText())
        val jsonResponse = json.decodeFromString<Response.AuthResponse>(response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("test@email.com", jsonResponse.data.email)
    }

    @Test
    fun testSignup() = testApplication {
        application {
            module(testing = true)
        }

        val response = client.post("/v1/signup") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(
                Json.encodeToString(
                    SignupDTO.serializer(),
                    SignupDTO("test1@email.com", "password", "test", "name")
                )
            )
        }
        val jsonResponse = json.decodeFromString<Response.AuthResponse>(response.bodyAsText())
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("test1@email.com", jsonResponse.data.email)
    }

}
