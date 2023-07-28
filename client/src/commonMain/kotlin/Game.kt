import com.github.quillraven.fleks.World
import korlibs.io.async.async
import korlibs.io.net.ws.WebSocketClient
import korlibs.korge.scene.Scene
import korlibs.korge.view.SContainer
import korlibs.korge.view.container
import korlibs.korge.view.scale
import korlibs.samples.clientserver.MS
import korlibs.time.DateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Game : Scene() {

    override suspend fun SContainer.sceneInit() {
    }

    override suspend fun SContainer.sceneMain() {
        SpriteAssets.load()
        println("A")
        println("scaleAvg2")
        injector.apply {
            mapSingleton { sceneContainer }
            mapSingleton {
                world(get()) {
                    injectables {
                        add("layer0", container().scale(5.0))
                    }
                    systems {
                        add(SpawningSystem)
                    }
                }
            }
        }
        println("B")

        println("C")
        injector.get<World>().apply {
            entity {
                it += Sprite(
                    image = "test",
                    animation = "",
                    layer = inject("layer0")
                )
            }
        }
        println("D")
        val socket = WebSocketClient("ws://localhost:8080/echo")
        socket.messageChannelString().send("asdf")
        socket.onAnyMessage.invoke {
            runCatching {
                println(it.toString())
//                val ms = Json.decodeFromString<MS>(it)
//                println(DateTime.now().unixMillisLong - ms.ms)
            }
        }
        repeat((0..100).count()) {
            async {
                socket.send(Json.encodeToString(MS()))
            }
        }

        println("F")
    }

}