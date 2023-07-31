package scene

import korlibs.image.font.Font
import korlibs.image.font.readWoffFont
import korlibs.image.text.TextAlignment
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.scene.Scene
import korlibs.korge.style.ViewStyles
import korlibs.korge.style.styles
import korlibs.korge.style.textAlignment
import korlibs.korge.style.textFont
import korlibs.korge.ui.uiContainer
import korlibs.korge.view.SContainer
import korlibs.korge.view.align.centerOnStage
import org.koin.mp.KoinPlatform.getKoin
import ui.mainMenu

val styler: ViewStyles.() -> Unit = {
    textFont = getKoin().get<Font>()
    textAlignment = TextAlignment.MIDDLE_CENTER

}

class MainScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        uiContainer {
            centerOnStage()
            styles(styler)
            mainMenu()
        }


    }
}