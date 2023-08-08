package model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import kotlinx.uuid.exposed.KotlinxUUIDTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Sessions : KotlinxUUIDTable() {
    val player = reference("player", OnlinePlayers)
    init { let { transaction { SchemaUtils.create(it) } } }
}

class Session(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<Session>(Sessions)
    var player by Sessions.player
}

fun newSession(sessionPlayer: OnlinePlayer) = transaction {
    Session.new {
        player = sessionPlayer.id
    }
}

fun getPlayerUUIDBySession(session: UUID) = transaction {
    Session.find(Sessions.id eq session).first().player.value
}

fun logout(sessionUUID: UUID) = transaction {
    val player = getPlayerBySession(sessionUUID)
    Session.find(Sessions.id eq sessionUUID).first().delete()
    player.delete()
}