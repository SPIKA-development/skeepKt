package old

import converter.PacketFrame
import converter.packetFrame
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.testing.*
import io.ktor.server.websocket.*
import io.ktor.server.websocket.WebSockets
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@OptIn(
    ExperimentalSerializationApi::class,
    ObsoleteCoroutinesApi::class,
    ExperimentalCoroutinesApi::class
)
internal class Test1 {

    @Test
    fun testRoot() = testApplication {
        install(WebSockets) {
            val a15Sec = 15.seconds.toJavaDuration()
            pingPeriod = a15Sec
            timeout = a15Sec
            contentConverter = KotlinxWebsocketSerializationConverter(ProtoBuf)
        }
        routing {
            webSocket {
                for (frame in incoming) {
                    frame.readBytes()
                    val packet = receiveDeserialized<PacketFrame>()
                    println(packet.deserialize() as MS
                    )
                }
            }
        }

        val websocket = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(ProtoBuf)
            }
        }.webSocketSession()
        repeat(10) {
            websocket.sendSerialized(packetFrame(MS()))
        }
        repeat(10) {
            val receivedMs = websocket.receiveDeserialized<PacketFrame>()
            println(receivedMs.deserialize())
        }
    }
}