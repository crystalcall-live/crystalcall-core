package utils

import io.ktor.server.application.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun getLogger(): Logger = LoggerFactory.getLogger(Application::class.java)