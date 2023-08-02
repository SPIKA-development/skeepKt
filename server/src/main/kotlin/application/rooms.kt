package application

import application.configuration.getPlayer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.uuid.UUID
import model.createRoom
import model.listRoom
import model.nameRoom

fun Application.configureRooms() {
    routing {
        authenticate {
            route("rooms") {
                post { call.respond(listRoom()) }
                post("create") { call.respond(createRoom(call.getPlayer())) }
                post("join/{uuid}") {
                    call.parameters["uuid"]
                    //todo
                    call.respond(HttpStatusCode.OK)
                }
                post("{uuid}/name") {
                    call.respond(nameRoom(UUID(call.parameters["uuid"]!!)))
                }
            }
        }
    }
}