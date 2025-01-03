import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv()

object Config {
    // Database
    val DB_URL: String = dotenv.get("DB_URL")
    val DB_DRIVER: String = dotenv.get("DB_DRIVER")
    val DB_USER: String = dotenv.get("DB_USER")
    val DB_PASSWORD: String = dotenv.get("DB_PASSWORD")
    val TEST_DB_URL: String = dotenv.get("TEST_DB_URL")

    // Admin
    val ADMIN_USER_EMAIL: String = dotenv.get("ADMIN_USER_EMAIL")
    val ADMIN_USER_PASSWORD: String = dotenv.get("ADMIN_USER_PASSWORD")

    // Google OAuth
    val CLIENT_ID: String = dotenv.get("CLIENT_ID")
    val CLIENT_SECRET: String = dotenv.get("CLIENT_SECRET")
    val TOKEN_ENDPOINT: String = dotenv.get("TOKEN_ENDPOINT")
    val REDIRECT_URI: String = dotenv.get("REDIRECT_URI")

    // JWT
    val JWT_SECRET: String = dotenv.get("JWT_SECRET")
    val JWT_AUDIENCE: String = dotenv.get("JWT_AUDIENCE")
    val JWT_ISSUER: String = dotenv.get("JWT_ISSUER")
    val JWT_REALM: String = dotenv.get("JWT_REALM")

    val DOMAIN: String = dotenv.get("DOMAIN")
}