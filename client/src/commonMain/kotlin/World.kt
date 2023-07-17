import com.github.quillraven.fleks.WorldConfiguration
import com.github.quillraven.fleks.world
import components.Sprite
import korlibs.korge.scene.SceneContainer
import korlibs.korge.view.addUpdater
import org.koin.core.definition.Callbacks

fun world(sceneContainer: SceneContainer, init: WorldConfiguration.() -> Unit = {}) = world {
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