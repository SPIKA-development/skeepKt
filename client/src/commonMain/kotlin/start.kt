import korlibs.image.font.Font
import korlibs.image.font.readWoffFont
import korlibs.image.format.*
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.Korge
import korlibs.korge.scene.SceneContainer
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.ScaleMode
import network.client
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin
import scene.MainScene
import util.ColorPalette
import websocket.startWebSocket
import kotlin.coroutines.coroutineContext

lateinit var sceneContainer: SceneContainer

suspend fun start() {
    val coroutineContext = coroutineContext
    val logo = resourcesVfs["images/logo.png"].readBitmapSlice()
    val font = resourcesVfs["fonts/NanumSquareNeoTTF-dEb.woff"].readWoffFont()
    getKoin().loadModules(listOf(module {
        single { coroutineContext }
        single { font } bind Font::class
        single(named("logo")) { logo }

    }))
    client()
    startWebSocket()
    Korge(title = "Skeep", icon = "images/logo.png", scaleMode = ScaleMode.SHOW_ALL, backgroundColor = ColorPalette.background) {
        sceneContainer = sceneContainer()
        sceneContainer.changeTo({ MainScene() })
    }
}
