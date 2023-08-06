package scene

import korlibs.image.font.Font
import korlibs.image.text.TextAlignment
import korlibs.korge.scene.Scene
import korlibs.korge.style.*
import korlibs.korge.view.*
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform.getKoin
import ui.loginMenu
import util.ColorPalette

val styler: ViewStyles.() -> Unit = {
    textFont = getKoin().get<Font>()
    textAlignment = TextAlignment.MIDDLE_CENTER
    textSize = 22f
    textColor = ColorPalette.text
}

class MainScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        sceneContainer.container {
            text(getKoin().get<String>(named("version")), textSize = 15f) {
                val padding = 10
                positionY(sceneContainer.height - height - padding)
                positionX(padding*2)
            }.zIndex(100)
            zIndex(100)
        }
        loginMenu(sceneContainer)
//        MainMenuState().mainMenu()
    }
}