package model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import kotlinx.uuid.exposed.KotlinxUUIDTable
import network.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

object Rooms : KotlinxUUIDTable() {
    val name = varchar("name", 50)
    val maxPlayers = integer("maxPlayers")
    val mode = enumeration<RoomMode>("mode")
    init { let { transaction { SchemaUtils.create(it) } } }
}

class Room(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<Room>(Rooms)
    var name by Rooms.name
    var maxPlayers by Rooms.maxPlayers
    var mode by Rooms.mode
}

fun listRoom() = transaction {
    Room.all().map {
        ViewedRoom(
            it.id.value,
            it.name,
            it.maxPlayers,
            OnlinePlayer.count(OnlinePlayers.room eq it.id).toInt(),
            it.mode
        )
    }
}

fun getJoinedPlayersAmount(room: UUID) = transaction {
    OnlinePlayer.count(OnlinePlayers.room eq room).toInt()
}


fun createRoom(creator: UUID, createRoom: CreateRoom) = transaction {
    if (createRoom.testLength().not()) {
        CreateRoomResult(CreateRoomResultType.NOT_ALlOWED_NAME)
    } else if (createRoom.testMaxPlayers().not()) {
        CreateRoomResult(CreateRoomResultType.NOT_ALLOWED_MAX_PLAYERS_AMOUNT)
    } else Room.new {
//        val onlinePlayer = OnlinePlayer.find(OnlinePlayers.id eq creator).first()
        name = createRoom.name
        maxPlayers = createRoom.maxPlayers
        mode = createRoom.roomMode

    }
        .run { ViewedRoom(id.value, name, maxPlayers, 0, createRoom.roomMode) }
        .run { CreateRoomResult(CreateRoomResultType.CREATED, room = uuid) }
}

fun nameRoom(room: UUID) = transaction {
    Room.find(Rooms.id eq room).first().name
}

fun joinRoom(player: UUID, room: UUID) = transaction {
    OnlinePlayer.find(OnlinePlayers.id eq player).first().room = EntityID(room, Rooms)
}

fun leaveRoom(session: UUID) = transaction {
    val player = getPlayerBySession(session)
    val room = player.room!!
    player.room = null
    if (getJoinedPlayersAmount(room.value) == 0) {
        Rooms.deleteWhere { Rooms.id eq room }
    }
}