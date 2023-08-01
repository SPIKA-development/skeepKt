package model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import kotlinx.uuid.exposed.KotlinxUUIDTable
import network.ListRoom
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
            it.name,
            it.maxPlayers,
            Player.count(Players.room eq it.id).toInt()
        )
    }
}

const val defaultRoomMaxPlayers = 6
fun createRoom(creator: UUID) = transaction {
    Room.new {
        println(Session.all().map { it.id })
        println(creator)
        val player = Player.find(Players.id eq creator).first()
        name = "${player.name}의 방"
        maxPlayers = 6
    }.run { ViewedRoom(name, maxPlayers, 0) }
}