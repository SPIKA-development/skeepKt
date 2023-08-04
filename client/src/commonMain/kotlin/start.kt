import korlibs.image.bitmap.slice
import korlibs.image.color.Colors
import korlibs.image.font.Font
import korlibs.image.font.readWoffFont
import korlibs.image.format.*
import korlibs.image.vector.format.readSVG
import korlibs.image.vector.render
import korlibs.image.vector.renderNoNative
import korlibs.image.vector.renderToImage
import korlibs.image.vector.scaled
import korlibs.io.file.std.cacheVfs
import korlibs.io.file.std.cachedToMemory
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.Korge
import korlibs.korge.scene.SceneContainer
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.ScaleMode
import korlibs.math.geom.Size
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
    val profile = resourcesVfs["images/profile.png"].readBitmap()
    getKoin().loadModules(listOf(module {
        single { coroutineContext }
        single { font } bind Font::class
        single(named("logo")) { logo }
        single(named("profile")) { profile }

    }))
    client()
    startWebSocket()
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
