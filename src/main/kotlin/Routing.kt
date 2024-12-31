import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.authenticationRouting
import routes.miscellaneousRouting
import java.io.File

fun Application.configureRouting() {
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("openapi/swagger")
    }

    routing {
        get("") {
            call.respond(MustacheContent("index.hbs", mapOf("title" to "Swagger UI")))
        }

        get("/documentation.yaml") {
            call.respondFile(File("src/main/resources/openapi/documentation.yaml"))
        }

        authenticationRouting()
        miscellaneousRouting()
    }
}
