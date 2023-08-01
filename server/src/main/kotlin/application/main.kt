package application

import io.ktor.server.engine.*
import io.ktor.server.netty.*

val server = embeddedServer(Netty, environment)

fun main() {
    server.start(wait = true)
}

fun startTestServer() = server.start()

