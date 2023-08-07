import korlibs.image.font.Font
import korlibs.image.font.readWoffFont
import korlibs.image.format.*
import korlibs.io.async.runBlockingNoSuspensions
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.Korge
import korlibs.korge.scene.SceneContainer
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.ScaleMode
import korlibs.math.geom.Size
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import network.client
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin
import scene.MainScene
import util.ColorPalette
import util.launchNow
import websocket.startWebSocket
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

lateinit var sceneContainer: SceneContainer

suspend fun start() {
    val coroutineContext = coroutineContext
    val logo = resourcesVfs["images/logo.png"].readBitmapSlice()
    val font = resourcesVfs["fonts/NanumSquareNeoTTF-dEb.woff"].readWoffFont()
    val boldFont = resourcesVfs["fonts/NanumSquareNeoTTF-eHv.woff"].readWoffFont()
    val profile = resourcesVfs["images/profile.png"].readBitmap()
    getKoin().loadModules(listOf(module {
        single { coroutineContext }
        single(named("bold")) { boldFont } bind Font::class
        single { font } bind Font::class
        single(named("logo")) { logo }
        single(named("profile")) { profile }

    }))
    Korge(
        windowSize = Size(960, 540),
        title = "Skeep",
        icon = "images/logo.png",
        scaleMode = ScaleMode.SHOW_ALL,
        backgroundColor = ColorPalette.background
    ) {
        sceneContainer = sceneContainer()
        sceneContainer.changeTo({ MainScene() })
    }
}
