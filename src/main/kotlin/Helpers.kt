import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dtos.Response
import io.ktor.server.application.*
import org.apache.commons.mail2.javax.HtmlEmail
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
    return Config.DOMAIN + groups.joinToString("-")
}

fun generateTokens(userClaim: Map<String, Any>): Pair<String, String> {
    val accessToken = JWT.create()
        .withAudience(Config.JWT_AUDIENCE)
        .withIssuer(Config.JWT_ISSUER)
        .withClaim("user", userClaim)
        .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 1000)) // Access token expires in 1 hour
        .sign(Algorithm.HMAC256(Config.JWT_SECRET))

    val refreshToken = JWT.create()
        .withAudience(Config.JWT_AUDIENCE)
        .withIssuer(Config.JWT_ISSUER)
        .withClaim("user", userClaim)
        .withExpiresAt(Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000)) // Refresh token expires in 3 days
        .sign(Algorithm.HMAC256(Config.JWT_SECRET))

    return Pair(accessToken, refreshToken)
}

fun refreshAccessToken(refreshToken: String): Response {
    try {
        val decodedJWT = JWT.require(Algorithm.HMAC256(Config.JWT_SECRET))
            .withAudience(Config.JWT_AUDIENCE)
            .withIssuer(Config.JWT_ISSUER)
            .build()
            .verify(refreshToken)


        if (decodedJWT.expiresAt == null || decodedJWT.expiresAt.before(Date())) {
            return Response.GenericResponse(status = "error", message = "Invalid expired token!")
        }


        val userClaim = decodedJWT.getClaim("user").let {
            it as Map<String, Any>
        }
        val accessToken = generateTokens(userClaim).first

        return Response.TokenResponse(
            status = "success",
            message = "Token refreshed successfully!",
            token = accessToken
        )
    } catch (e: Exception) {
        return Response.GenericResponse(status = "error", message = "Error refreshing/verifying token: $e")
    }
}

fun sendEmail(email: String, name: String) {
    val htmlEmail = HtmlEmail()
    htmlEmail.hostName = Config.EMAIL_HOST_NAME
    htmlEmail.setStartTLSEnabled(true)
    htmlEmail.setSmtpPort(Config.EMAIL_PORT)
    htmlEmail.setAuthentication(Config.EMAIL_USERNAME, Config.EMAIL_PASSWORD)
    htmlEmail.addTo(email, name)
    htmlEmail.setFrom("MS_0i0uEB@daimones.xyz", "CrystalCall")
    htmlEmail.setSubject("CrystalCall: Reset Your Password")

    val firstName = name.split(" ")[0]
    htmlEmail.setHtmlMsg(
        """
        <html>
        <body>
        
        <p>Hi ${firstName},</p>
        
        <p>We received a request to reset the password for your account. If you didn't make this request, 
        you can ignore this email - your password won't change.</p>
        
        <p>To reset your password:</p>
        <p>Click the secure link below (valid for 30 minutes):
        <a href='https://crystalcall.daimones.xyz/auth/forgot-password?id=${email}'>link</a>.
        You'll be taken to a page where you can create a new password.</p>
        
        <p>For your security:</p>
        <p>This link will expire in 30 minutes.
        Use a strong password that you haven't used before.
        Never share your password with anyone</p>
        
        <p>If you have any issues or didn't request this reset, please contact our support team immediately at 
        <a href='mailto:support@crystalcall.live'>support@crystalcall.live</a>.</p>
        
        <div style="margin-top: 20px; line-height: 0.8;">
            <p style="margin: 8px 0;">Best regards,</p>
            <p style="margin: 8px 0;">CrystalCall Team</p>
        </div>
        <p style="font-size: 12px;">Note: This is an automated message. Please do not reply to this email.</p>
        </body>
        </html>
    """
    )

    // Alternative message
    htmlEmail.setTextMsg("Your email client does not support HTML messages")

    htmlEmail.send()
}