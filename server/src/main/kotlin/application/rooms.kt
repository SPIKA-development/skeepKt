package application

import application.configuration.getPlayer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
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
                post("join") {
                    //todo
                    call.respond(HttpStatusCode.OK)
                }
                post("name") { call.respond(nameRoom(call.receive<UUID>())) }
            }
        }
    }
}