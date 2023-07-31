package application

import io.github.bruce0203.skeep.model.LoginRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.sessions.serialization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.uuid.UUID
import model.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class UserSession(val uuid: UUID) : Principal

fun Application.configureAuthentication() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 10
            serializer = KotlinxSessionSerializer(Json)
        }
    }
    install(Authentication) {
        session<UserSession> {
            validate { session ->
                println("session=$session")
                println(transaction { Session.all().map { it.id } })
                //throw exception when not found
                transaction { Session.find(model.Sessions.id eq session.uuid) }
                session.copy()
            }
            challenge {
                call.respondText("login failed", status = HttpStatusCode.Unauthorized)
            }
        }
    }
    routing {
        get("login") {
            val loginRequest = call.receive<LoginRequest>()
            val sessionPlayer = try {
                newPlayer(loginRequest.username)
            } catch (e: Throwable) {
                e.printStackTrace()

                call.respondText("Username is already taken by other", status = HttpStatusCode.NotAcceptable)
                return@get
            }
            call.sessions.set(newSession(sessionPlayer).toUserSession())
            call.respondText("Logged in")
        }
        get("logout") {
            call.sessions.clear(call.sessions.findName(UserSession::class))
        }
    }

}

suspend fun ApplicationCall.getUserSession(): UUID {
    val userSession = sessions.get<UserSession>()
    if (userSession === null) {
        respondText("session is invalid", status = HttpStatusCode.Unauthorized)
        throw AssertionError("user session is null")
    } else {
        return userSession.uuid
    }
}

suspend fun ApplicationCall.getPlayer() = getPlayerBySession(getUserSession())