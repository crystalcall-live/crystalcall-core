package utils

import Config
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

fun getLogger(): Logger = LoggerFactory.getLogger(Application::class.java)

fun generateMeetingLink(): String {
    val chars = ('a'..'z')
    val groupSize = 3
    val groups = (1..3).map {
        (1..groupSize).map { chars.random() }.joinToString("")
    }
    return groups.joinToString("-")
}

fun generateTokens(userEmail: String): Pair<String, String> {
    val accessToken = JWT.create()
        .withAudience(Config.JWT_AUDIENCE)
        .withIssuer(Config.JWT_ISSUER)
        .withClaim("email", userEmail)
        .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 1000)) // Access token expires in 1 hour
        .sign(Algorithm.HMAC256(Config.JWT_SECRET))

    val refreshToken = JWT.create()
        .withAudience(Config.JWT_AUDIENCE)
        .withIssuer(Config.JWT_ISSUER)
        .withClaim("email", userEmail)
        .withExpiresAt(Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000)) // Refresh token expires in 3 days
        .sign(Algorithm.HMAC256(Config.JWT_SECRET))

    return Pair(accessToken, refreshToken)
}

fun refreshAccessToken(refreshToken: String): String? {
    try {
        val decodedJWT = JWT.require(Algorithm.HMAC256(Config.JWT_SECRET))
            .withAudience(Config.JWT_AUDIENCE)
            .withIssuer(Config.JWT_ISSUER)
            .build()
            .verify(refreshToken)

        val userEmail = decodedJWT.getClaim("email").asString()

        return generateTokens(userEmail).first
    } catch (e: Exception) {
        return null
    }
}