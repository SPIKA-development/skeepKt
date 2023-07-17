import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import scene.Game

suspend fun main() = Korge {
    sceneContainer().changeTo({ Game() })
}