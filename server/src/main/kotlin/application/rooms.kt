package application

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.createRoom
import model.listRoom

fun Application.configureRooms() {
    routing {
        authenticate {
            route("rooms") {
                get { call.respond(listRoom()) }
                get("create") {
                    call.respond(createRoom(call.getPlayer()))
                }
            }
        }
    }

}