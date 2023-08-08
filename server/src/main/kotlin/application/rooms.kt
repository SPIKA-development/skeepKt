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
import network.CreateRoom
import network.PlayerJoinPacket
import network.PlayerLeavePacket
import network.ServerPacket
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRooms() {
    routing {
        authenticate {
            route("rooms") {
                post { call.respond(listRoom()) }
                post("create") {
                    val createRoom = call.receive<CreateRoom>()
                    call.respond(createRoom(call.getPlayer(), createRoom))
                }
                post("join") {
                    val room = call.receive<UUID>()
                    val maxPlayers = transaction { Room.find(Rooms.id eq room).first().maxPlayers }
                    if (getJoinedPlayersAmount(room) >= maxPlayers) {
                        call.respond(HttpStatusCode.ServiceUnavailable)
                        return@post
                    }
                    listRoom()
                    joinRoom(call.getPlayer(), room)
                    val player = getPlayerBySession(call.getUserSession())
                    getRoomConnections(room).forEach {
                        it.websocket.sendToClient(ServerPacket.PLAYER_JOIN, PlayerJoinPacket(player.name))
                    }
                    call.respond(HttpStatusCode.OK)
                }
                post("name") { call.respond(nameRoom(call.receive<UUID>())) }
                post("players") {
                    call.respond(getPlayersByRoom(getPlayerBySession(call.getUserSession()).room!!.value))
                }
                post("leave") {
                    val player = getPlayerBySession(call.getUserSession())
                    leaveRoom(call.getUserSession())
                    getRoomConnections(player.room!!.value).forEach {
                        it.websocket.sendToClient(ServerPacket.PLAYER_LEAVE, PlayerLeavePacket(player.name))
                    }
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}