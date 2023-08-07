package model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import kotlinx.uuid.exposed.KotlinxUUIDTable
import network.ViewedPlayer
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object OnlinePlayers : KotlinxUUIDTable() {
    val name = varchar("name", 16)
    var room = reference("joined_room", Rooms).nullable().default(null)
    init { let { transaction { SchemaUtils.create(it) } } }
}

class OnlinePlayer(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<OnlinePlayer>(OnlinePlayers)
    var name by OnlinePlayers.name
    var room by OnlinePlayers.room
}

object PlayerAlreadyExistsException : Exception() {init { stackTrace = arrayOf() } }
fun newPlayer(newPlayerName: String) = transaction {
    if (OnlinePlayer.find(OnlinePlayers.name eq newPlayerName).any()) throw PlayerAlreadyExistsException
    OnlinePlayer.new {
        name = newPlayerName
    }
}

fun getPlayerBySession(session: UUID) = transaction {
    OnlinePlayer.find(OnlinePlayers.id eq getPlayerUUIDBySession(session)).first()
}

fun getPlayersByRoom(room: UUID) = transaction {
    OnlinePlayer.find(OnlinePlayers.room eq room).map { ViewedPlayer(it.name) }
}