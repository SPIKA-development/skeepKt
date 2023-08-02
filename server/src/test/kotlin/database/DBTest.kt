package database

import application.configuration.configureDatabase
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import model.Session
import model.Sessions
import model.newPlayer
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test

class DBTest {

    @Test
    fun test() {
        configureDatabase()
        val newPlayer = newPlayer(UUID.generateUUID().toString().substring(0, 4))
        val uuid = UUID.generateUUID()
        transaction {
            Session.new {
            this.player = newPlayer.id
        } }
        val updatedUuid = transaction {
            Session.get(EntityID(uuid, Sessions)).player
        }
        println(uuid)
        println(updatedUuid)
    }
}