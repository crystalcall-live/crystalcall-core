import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(testing: Boolean = false) {
    configureSockets()
    configureSerialisation()
    configureHTTP()
    configureSecurity()
    configureRouting()
    configureDatabase(testing)
}
