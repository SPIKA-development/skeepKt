package application

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import java.io.File

const val serverPort = 8080

val server = embeddedServer(Netty, port = serverPort) {
    install(ContentNegotiation) {
        json()
    }
    routing {
        staticResources("/", "/distributions")
    }
    configureDatabase()
    configureAuthentication()
    configureRooms()
    configureWebsocket()
}

fun main() {
    server.start(wait = true)
}

fun startTestServer() = server.start()

