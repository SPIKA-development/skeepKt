import korlibs.image.color.Colors
import korlibs.image.font.Font
import korlibs.image.font.readWoffFont
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.ScaleMode
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin
import scene.MainScene
import kotlin.coroutines.coroutineContext

suspend fun start() {
    val coroutineContext = coroutineContext
    val font = resourcesVfs["fonts/NanumSquareNeoTTF-dEb.woff"].readWoffFont()
    getKoin().loadModules(listOf(module {
        single { coroutineContext }
        single { font } bind Font::class
    }))

    Korge(scaleMode = ScaleMode.COVER, backgroundColor = Colors.SLATEGRAY) {
        sceneContainer().changeTo({ MainScene() })
    }
}
