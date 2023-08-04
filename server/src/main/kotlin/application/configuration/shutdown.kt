package application.configuration

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*

fun Application.configurationShutdown() {
    val shutdown = ShutDownUrl("") { 0 }
    routing {
        post("shutdown") {
            val key = System.getenv("ADMIN_KEY")
            if (key.isEmpty()) return@post
            if (call.parameters["key"] == key) {
                shutdown.doShutdown(call)
            }
        }
    }
}