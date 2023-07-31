package application

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

const val serverPort = 8080

val server = embeddedServer(Netty, port = serverPort) {
    install(ContentNegotiation) {
        json()
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

