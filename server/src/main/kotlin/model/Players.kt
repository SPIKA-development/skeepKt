package model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import kotlinx.uuid.exposed.KotlinxUUIDTable
import kotlinx.uuid.exposed.kotlinxUUID
import kotlinx.uuid.generateUUID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Players : KotlinxUUIDTable() {
    val name = varchar("name", 16)
    var room = reference("joined_room", Rooms).nullable().default(null)
    init { let { transaction { SchemaUtils.create(it) } } }
}

class Player(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<Player>(Players)
    var name by Players.name
    var room by Players.room
}

fun newPlayer(newPlayerName: String) = transaction {
    Player.new {
        name = newPlayerName
    }
}

fun getPlayerBySession(session: UUID) = transaction {
    Player.find(Players.id eq getPlayerUUIDBySession(session)).first()
}