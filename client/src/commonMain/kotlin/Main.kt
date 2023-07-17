import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer

suspend fun main() = Korge {
    sceneContainer().changeTo({ Game() })
}