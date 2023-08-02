package application

import application.configuration.configureAuthentication
import application.configuration.configureDatabase
import application.configuration.environment
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*

val server = embeddedServer(Netty, environment)

fun main() {
    server.start(wait = true)
}

fun startTestServer() = server.start()


fun Application.module() {
    install(CORS) {
        anyHost()
        HttpMethod.DefaultMethods.forEach(::allowMethod)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }
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