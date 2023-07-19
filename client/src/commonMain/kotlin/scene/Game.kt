package scene

import SpriteAssets
import system.SpawningSystem
import com.github.quillraven.fleks.World
import components.Sprite
import korlibs.image.format.ASE
import korlibs.image.format.readImageDataContainer
import korlibs.image.format.toProps
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.math.geom.Size
import world

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
    }

}
