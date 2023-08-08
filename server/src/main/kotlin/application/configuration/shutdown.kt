package application.configuration

import application.connections
import application.sendToClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import network.ServerClosedPacket
import network.ServerPacket
import kotlin.system.exitProcess

fun Application.configurationShutdown() {
    routing {
        post("shutdown") {
            val key = EnvVar.ADMIN_KEY
            if (call.receiveText() == key) {
                connections.forEach {
                    it.websocket.sendToClient(ServerPacket.SERVER_CLOSED, ServerClosedPacket())
                }
                doShutdown(call)
            }
        }
    }
}

public suspend fun doShutdown(call: ApplicationCall) {
    call.application.log.warn("Shutdown URL was called: server is going down")
    val application = call.application
    val environment = application.environment
    val exitCode = 0

    val latch = CompletableDeferred<Nothing>()
    call.application.launch {
        latch.join()

        environment.monitor.raise(ApplicationStopPreparing, environment)
        if (environment is ApplicationEngineEnvironment) {
            environment.stop()
        } else {
            application.dispose()
        }

        exitProcess(exitCode)
    }

    try {
        call.respond(HttpStatusCode.Gone)
    } finally {
        latch.cancel()
    }
}