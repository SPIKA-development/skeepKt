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

class Sprite(
    val image: String,
    val animation: String,
    val layer: Container
) : Component<Sprite> {

    var isPlaying: Boolean = false
    var imageAnimView: ImageAnimationView<Image> =
        ImageAnimationView { Image(Bitmaps.transparent) }.apply { smoothing = false }
    override fun type(): ComponentType<Sprite> = Sprite

    companion object : ComponentType<Sprite>() {
        val onComponentAdded: ComponentHook<Sprite> = { entity, component ->
            val world = this
            component.apply {
                val image = SpriteAssets.getImage(image)
                val animations = image.animationsByName
                println(image.defaultAnimation.firstFrame)
                imageAnimView.animation = animations[animation]?: image.defaultAnimation
                imageAnimView.onPlayFinished = { world -= entity }
                imageAnimView.addTo(layer)
                imageAnimView.centerOnStage()
            }
        }
        val onComponentRemoved: ComponentHook<Sprite> = { entity, component ->
            component.imageAnimView.removeFromParent()
        }
    }
}