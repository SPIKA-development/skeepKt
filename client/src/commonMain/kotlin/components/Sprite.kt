package components

import SpriteAssets
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentHook
import com.github.quillraven.fleks.ComponentType
import korlibs.image.bitmap.Bitmaps
import korlibs.korge.view.Container
import korlibs.korge.view.Image
import korlibs.korge.view.addTo
import korlibs.korge.view.align.centerOnStage
import korlibs.korge.view.animation.ImageAnimationView
import korlibs.korge.view.animation.imageAnimationView

class Sprite(
    val image: String,
    val animation: String,
    val layer: Container
) : Component<Sprite> {

    var isPlaying: Boolean = false
    lateinit var imageAnimView: ImageAnimationView<Image>
    override fun type(): ComponentType<Sprite> = Sprite

    companion object : ComponentType<Sprite>() {
        val onComponentAdded: ComponentHook<Sprite> = world@{ entity, component ->
            component.apply {
                val image = SpriteAssets.getImage(image)
                val animations = image.animationsByName
                val animation = animations[animation]?: image.defaultAnimation
                imageAnimView = layer.imageAnimationView(animation).apply {
                    smoothing = false
                    scaleAvg = 3f
                    onPlayFinished = { this@world -= entity }
                    centerOnStage()
                }
            }
        }
        val onComponentRemoved: ComponentHook<Sprite> = { entity, component ->
            component.imageAnimView.removeFromParent()
        }
    }
}