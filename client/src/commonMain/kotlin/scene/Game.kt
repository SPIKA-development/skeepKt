package scene

import system.SpawningSystem
import com.github.quillraven.fleks.World
import components.Sprite
import korlibs.korge.scene.*
import korlibs.korge.view.*
import world

class Game : Scene() {

    override suspend fun SContainer.sceneInit() {
        scaleAvg = 4.5f
        injector.apply {
            mapSingleton { coroutineContext }
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
    }

    override suspend fun SContainer.sceneMain() {
        injector.get<World>().apply {
            entity {
                it += Sprite(
                    image = "test",
                    animation = "",
                    layer = inject("layer0")
                )
            }
        }
    }

}
