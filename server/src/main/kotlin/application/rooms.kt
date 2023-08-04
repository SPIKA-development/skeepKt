package application

import application.configuration.getPlayer
import application.configuration.getUserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.uuid.UUID
import model.*
import network.PlayerJoinPacket
import network.PlayerLeavePacket
import network.ServerPacket

fun Application.configureRooms() {
    routing {
        authenticate {
            route("rooms") {
                post { call.respond(listRoom()) }
                post("create") { call.respond(createRoom(call.getPlayer())) }
                post("join") {
                    joinRoom(call.getPlayer(), call.receive<UUID>())
                    val player = getPlayerBySession(call.getUserSession())
                    getPlayersByRoom(player.room!!.value).forEach {
                        it.websocket.sendToClient(ServerPacket.PLAYER_JOIN, PlayerJoinPacket(player.name))
                    }
                    call.respond(HttpStatusCode.OK)
                }
                post("name") { call.respond(nameRoom(call.receive<UUID>())) }
                post("leave") {
                    leaveRoom(call.getUserSession())
                    val player = getPlayerBySession(call.getUserSession())
                    getPlayersByRoom(player.room!!.value).forEach {
                        it.websocket.sendToClient(ServerPacket.PLAYER_LEAVE, PlayerLeavePacket(player.name))
                    }
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}