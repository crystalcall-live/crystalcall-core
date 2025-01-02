import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import io.ktor.server.application.*
import org.slf4j.LoggerFactory


fun Application.configureLogger() {
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    loggerContext.reset()
    val configurator = JoranConfigurator()
    configurator.context = loggerContext
    configurator.doConfigure(ClassLoader.getSystemResourceAsStream("logback.xml"))
}