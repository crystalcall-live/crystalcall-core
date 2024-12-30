import io.ktor.server.application.*
import models.Users
import org.ktorm.database.Database
import org.ktorm.dsl.insert
import org.ktorm.logging.Slf4jLoggerAdapter
import java.time.LocalDateTime

fun Application.configureDatabase(): Database {
    val database = Database.connect(
        url = Config.DB_URL,
        driver = Config.DB_DRIVER,
        user = Config.DB_USER,
        password = Config.DB_PASSWORD,
        logger = Slf4jLoggerAdapter("crystal-logger")
    )

    // Execute the drop/create table SQL
    database.useConnection{ connection ->
        val dropTableSQL = "drop table if exists t_user CASCADE".trim()
        connection.prepareStatement(dropTableSQL).execute()

        val createTableSQL = """
            create table if not exists t_user (
                id SERIAL PRIMARY KEY NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                is_active BOOLEAN DEFAULT false,
                created TIMESTAMP NOT NULL, 
                modified TIMESTAMP NOT NULL
            )
        """.trim()
        connection.prepareStatement(createTableSQL).execute()
    }

    database.useTransaction {
        database.insert(Users){
            set(it.email, "test@email.com")
            set(it.password, "password")
            set(it.created, LocalDateTime.now())
            set(it.modified, LocalDateTime.now())
        }
    }

    return database
}