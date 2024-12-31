import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv()

object Config {
    val DB_URL: String = dotenv.get("DB_URL")
    val DB_DRIVER: String = dotenv.get("DB_DRIVER")
    val DB_USER: String = dotenv.get("DB_USER")
    val DB_PASSWORD: String = dotenv.get("DB_PASSWORD")

    val CLIENT_ID: String = dotenv.get("CLIENT_ID")
    val CLIENT_SECRET: String = dotenv.get("CLIENT_SECRET")
    val TOKEN_ENDPOINT: String = dotenv.get("TOKEN_ENDPOINT")
    val REDIRECT_URI: String = dotenv.get("REDIRECT_URI")
}