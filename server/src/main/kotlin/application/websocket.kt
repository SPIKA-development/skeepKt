package application

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import java.time.Duration

fun Application.configureWebsocket() {
    install(WebSockets) {
        val _15sec = Duration.ofSeconds(15)
        pingPeriod = _15sec
        timeout = _15sec
    }
}