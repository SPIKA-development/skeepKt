import korlibs.image.font.Font
import korlibs.image.font.readWoffFont
import korlibs.image.format.readBitmap
import korlibs.image.format.readBitmapSlice
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.Korge
import korlibs.korge.scene.SceneContainer
import korlibs.korge.scene.sceneContainer
import korlibs.korge.view.*
import korlibs.math.geom.Anchor
import korlibs.math.geom.ScaleMode
import korlibs.math.geom.Size
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin
import util.ColorPalette
import kotlin.coroutines.coroutineContext

lateinit var scene: SceneContainer
lateinit var screen: FixedSizeContainer

suspend fun startMain() {
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
        windowSize = Size(512, 512),
        title = "Skeep",
        icon = "images/logo.png",
        scaleMode = ScaleMode.NO_SCALE,
        clipBorders = false,
        scaleAnchor = Anchor.TOP_LEFT,
        backgroundColor = ColorPalette.background
    ) {
        scene = sceneContainer()
        scene.changeTo({ MainScene() })
    }
}

inline fun <reified T : List<*>> onAdd(hook: List<T>) {
}
