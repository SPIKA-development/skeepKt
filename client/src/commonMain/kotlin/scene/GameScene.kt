package scene

import SpriteAssets
import com.github.quillraven.fleks.World
import entity.SpawningSystem
import entity.Sprite
import entity.world
import korlibs.korge.scene.Scene
import korlibs.korge.view.SContainer
import korlibs.korge.view.container
import korlibs.korge.view.scale

class GameScene : Scene() {

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
        println("F")
    }

}