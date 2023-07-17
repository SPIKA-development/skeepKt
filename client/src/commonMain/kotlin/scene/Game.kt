package scene

import system.SpawningSystem
import com.github.quillraven.fleks.World
import components.Sprite
import korlibs.korge.scene.*
import korlibs.korge.view.*
import world

class Game : Scene() {

    override suspend fun SContainer.sceneInit() {
        println("A")
        scaleAvg = 4.5f
        injector.apply {
            mapSingleton { sceneContainer }
            mapSingleton {
                world(get()) {
                    injectables {
                        add("layer0", container())
                    }
                    systems {
                        add(SpawningSystem)
                    }
                }
            }
        }
        println("B")
    }

    override suspend fun SContainer.sceneMain() {
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
    }

}
