package scene

import SpawningSystem
import SpriteAssets
import ViewLayer
import com.github.quillraven.fleks.World
import components.Sprite
import korlibs.korge.scene.*
import korlibs.korge.view.*
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin
import world

class Game : Scene() {

    override suspend fun SContainer.sceneInit() {
        scaleAvg = 4.5f
        startKoin {
            modules(modules = module(createdAtStart = true) {
                single { coroutineContext }
                single { sceneContainer }
                singleOf(::SpriteAssets)
                ViewLayer.values().forEachIndexed { index, elem ->
                    single(named(elem)) { container().zIndex(index) }
                }
                single {
                    world(get()) {
                        systems {
                            add(SpawningSystem)
                        }
                    }
                }
            })
        }
    }

    override suspend fun SContainer.sceneMain() {
        getKoin().get<World>().apply {
            entity {
                it += Sprite(
                    image = "test",
                    animation = "",
                    layer = ViewLayer.CREATURE
                )
            }
        }
    }

}
