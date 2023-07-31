package entity

import com.github.quillraven.fleks.WorldConfiguration
import com.github.quillraven.fleks.configureWorld
import korlibs.korge.scene.SceneContainer
import korlibs.korge.view.addUpdater

fun world(sceneContainer: SceneContainer, init: WorldConfiguration.() -> Unit = {}) = configureWorld {
    components {

        onAdd(Sprite, Sprite.onComponentAdded)
        onRemove(Sprite, Sprite.onComponentRemoved)
    }
    families {

    }
    init()
}.also { world ->
    sceneContainer.addUpdater {
        world.update(it.seconds.toFloat())
    }
}