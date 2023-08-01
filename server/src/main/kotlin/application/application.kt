package application

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        staticResources("/", "/")
    }
    configureDatabase()
    configureAuthentication()
    configureRooms()
    configureWebsocket()
}