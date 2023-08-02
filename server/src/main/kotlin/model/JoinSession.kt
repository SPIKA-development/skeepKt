package model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import kotlinx.uuid.exposed.KotlinxUUIDTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object JoinSessions : KotlinxUUIDTable() {
    val room = reference("room", Rooms)
    val player = reference("player", Players)
    init { let { transaction { SchemaUtils.create(it) } } }
}

class JoinSession(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<JoinSession>(JoinSessions)
    var room by JoinSessions.room
    var player by JoinSessions.player
}

