import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.io.use
import io.ktor.server.websocket.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import korlibs.samples.clientserver.MS
import korlibs.samples.clientserver.Pos
import korlibs.samples.clientserver.TeleportPacket
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import kotlin.reflect.full.createType

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        routing {
            webSocket("/echo") {
                send(Json.encodeToString<TeleportPacket>(TeleportPacket(Pos(100, 100))))
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    println(receivedText)
//                    val ms = Json.decodeFromString<MS>(receivedText)
//                    println(ms)
                    if (receivedText.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    } else {
                        send(receivedText)
                    }
                }

            }
        }

    }.start(wait = true)
}

fun udp() {
    runBlocking {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val server = aSocket(selectorManager)
            .udp()
            .bind(InetSocketAddress("127.0.0.1", 8080))
        println("Server is listening at ${server.localAddress}")
        server.use {
            val receiveChannel = server.openReadChannel()
            while (true) {
                try {
                    val datagram = server.receive()
                    val builder = BytePacketBuilder()
                    builder.writeText(datagram.packet.readUTF8Line()!!)
                    server.send(Datagram(builder.build(), datagram.address))
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }
}