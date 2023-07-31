package model

import application.UserSession
import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import kotlinx.uuid.exposed.KotlinxUUIDTable
import kotlinx.uuid.exposed.kotlinxUUID
import kotlinx.uuid.generateUUID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Sessions : KotlinxUUIDTable() {
    val player = reference("player", Players)
    init { let { transaction { SchemaUtils.create(it) } } }
}

class Session(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<Session>(Sessions)
    var player by Sessions.player
}

fun Session.toUserSession() = UserSession(id.value)

fun newSession(sessionPlayer: Player) = transaction {
    Session.new {
        player = sessionPlayer.id
    }
}

fun getPlayerBySession(session: UUID) = transaction {
    Session.find(Sessions.id eq session).first().player.value
}