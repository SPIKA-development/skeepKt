package model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import kotlinx.uuid.exposed.KotlinxUUIDTable
import network.ViewedRoom
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Rooms : KotlinxUUIDTable() {
    val name = varchar("name", 50)
    val maxPlayers = integer("maxPlayers")
    init { let { transaction { SchemaUtils.create(it) } } }
}

class Room(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<Room>(Rooms)
    var name by Rooms.name
    var maxPlayers by Rooms.maxPlayers
}

fun listRoom() = transaction {
    Room.all().map {
        ViewedRoom(
            it.id.value,
            it.name,
            it.maxPlayers,
            OnlinePlayer.count(OnlinePlayers.room eq it.id).toInt()
        )
    }
}

fun getJoinedPlayersAmount(room: UUID) = transaction {
    OnlinePlayer.count(OnlinePlayers.room eq room).toInt()
}

const val defaultRoomMaxPlayers = 6
fun createRoom(creator: UUID) = transaction {
    Room.new {
        val onlinePlayer = OnlinePlayer.find(OnlinePlayers.id eq creator).first()
        name = "${onlinePlayer.name}의 방"
        maxPlayers = 6
    }.run { ViewedRoom(id.value, name, maxPlayers, 0) }
}

fun nameRoom(room: UUID) = transaction {
    Room.find(Rooms.id eq room).first().name
}

fun joinRoom(player: UUID, room: UUID) = transaction {
    OnlinePlayer.find(OnlinePlayers.id eq player).first().room = EntityID(room, Rooms)
}

fun leaveRoom(session: UUID) = transaction {
    getPlayerBySession(session).room = null
}