package application

import application.configuration.configurationShutdown
import application.configuration.configureAuthentication
import application.configuration.configureDatabase
import application.configuration.environment
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.*
import network.ClientEngineFactory
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.mp.KoinPlatform.getKoin
import org.koin.mp.KoinPlatform.startKoin

val server = embeddedServer(Netty, environment)

fun main() { server.start(wait = true) }

fun startTestServer() = server.start()

fun Application.module() {
    install(CachingHeaders) {
        options { call, content -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 0)) }
    }
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
    configurationShutdown()
    configureDatabase()
    configureAuthentication()
    configureRooms()
    configureWebsocket()
}