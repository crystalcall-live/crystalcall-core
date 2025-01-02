import io.ktor.server.application.*
import models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import utils.getLogger
import java.time.LocalDateTime

fun Application.configureDatabase(testing: Boolean = false) {
    val logger = getLogger()
    val dbUrl: String = if (!testing) Config.DB_URL else Config.TEST_DB_URL
    
    Database.connect(
        url = dbUrl,
        driver = Config.DB_DRIVER,
        user = Config.DB_USER,
        password = Config.DB_PASSWORD,
    )

    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.drop(Users)
        SchemaUtils.create(Users)

        try {
            val hashedPwd = BCrypt.hashpw(Config.ADMIN_USER_PASSWORD, BCrypt.gensalt())
            val id = Users.insertAndGetId {
                it[email] = Config.ADMIN_USER_EMAIL
                it[firstName] = "Test"
                it[lastName] = "User"
                it[password] = hashedPwd
                it[created] = LocalDateTime.now()
                it[modified] = LocalDateTime.now()
            }
            logger.info("Created admin user instance successfully with id $id")
        } catch (e: Exception) {
            throw Exception("Database Error: $e")
        }
    }
}