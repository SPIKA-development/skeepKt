import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.ScaleMode
import scene.Game

suspend fun main() = Korge(scaleMode = ScaleMode.COVER, backgroundColor = Colors.PAPAYAWHIP) {
    sceneContainer().changeTo({ Game() })
}