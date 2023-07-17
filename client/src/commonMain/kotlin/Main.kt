import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import scene.Game

suspend fun main() = Korge(backgroundColor = Colors.BLACK) {
    sceneContainer().changeTo({ Game() })
}