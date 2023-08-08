package application.configuration

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import model.*
import network.LoginRequest
import network.LoginResult
import network.LoginResultType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class UserSession(val uuid: UUID) : Principal

fun Application.configureAuthentication() {
    install(Authentication) {
        basic {
            validate { session ->
                //throw exception when not found
                val uuid = UUID(session.password)
                runCatching { transaction {
                    Session.find(model.Sessions.id eq uuid).first().id.value.run(::UserSession)
                } }.getOrNull()
            }
        }
    }
    routing {
        post("login") login@{
            val loginRequest = call.receive<LoginRequest>()
            val sessionPlayer = try {
                newPlayer(loginRequest.username)
            } catch (_: PlayerAlreadyExistsException) {
                call.respond(LoginResult(LoginResultType.ALREADY_JOINED))
                return@login
            } catch (e: Throwable) {
                e.printStackTrace()
                call.respond(LoginResult(LoginResultType.SERVER_IS_NOT_AVAILABLE))
                return@login
            }
            val uuid = newSession(sessionPlayer).id.value
            call.respond(LoginResult(LoginResultType.SUCCESS, uuid))
        }
    }

}

suspend fun ApplicationCall.getUserSession(): UUID {
    val userSession = principal<UserSession>()
    if (userSession === null) {
        respondText("session is invalid", status = HttpStatusCode.Unauthorized)
        throw AssertionError("user session is null")
    } else {
        return userSession.uuid
    }
}

suspend fun ApplicationCall.getPlayer() = getPlayerUUIDBySession(getUserSession())