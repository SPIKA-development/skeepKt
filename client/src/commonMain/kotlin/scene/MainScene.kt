package scene

import korlibs.image.font.Font
import korlibs.image.font.readWoffFont
import korlibs.image.text.TextAlignment
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.scene.Scene
import korlibs.korge.scene.SceneContainer
import korlibs.korge.scene.sceneContainer
import korlibs.korge.style.*
import korlibs.korge.ui.uiContainer
import korlibs.korge.view.SContainer
import korlibs.korge.view.align.centerOnStage
import network.login
import org.koin.mp.KoinPlatform.getKoin
import ui.MainMenuState
import ui.mainMenu
import util.ColorPalette

val styler: ViewStyles.() -> Unit = {
    textFont = getKoin().get<Font>()
    textAlignment = TextAlignment.MIDDLE_CENTER
    textSize = 22f
    textColor = ColorPalette.text
}

class MainScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        login()
        MainMenuState().mainMenu()
    }
}