import io.ktor.server.application.*

fun Application.configureSecurity() {
//    val jwtAudience = environment.config.property("jwt.audience").getString()
//    val jwtIssuer = environment.config.property("jwt.issuer").getString()
//    val jwtRealm = environment.config.property("jwt.realm").getString()
//    val jwtSecret = Config.JWT_SECRET
//    authentication {
//        jwt {
//            realm = jwtRealm
//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256(jwtSecret))
//                    .withAudience(jwtAudience)
//                    .withIssuer(jwtIssuer)
//                    .build()
//            )
//            validate { credential ->
//                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
//            }
//            challenge { defaultScheme, realm ->
//                call.respond(
//                    HttpStatusCode.Unauthorized,
//                    Response.GenericResponse(status = "error", message = "Token is not valid or has expired")
//                )
//            }
//        }
//    }
}
