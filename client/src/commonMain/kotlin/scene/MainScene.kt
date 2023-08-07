package scene

import event.PacketEvent
import korlibs.image.font.Font
import korlibs.image.text.TextAlignment
import korlibs.korge.scene.Scene
import korlibs.korge.style.*
import korlibs.korge.view.*
import network.ServerClosedPacket
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform.getKoin
import ui.loadingMenu
import ui.loginMenu
import util.ColorPalette
import util.launchNow

val styler: ViewStyles.() -> Unit = {
    textFont = getKoin().get<Font>()
    textAlignment = TextAlignment.MIDDLE_CENTER
    textSize = 22f
    textColor = ColorPalette.text
}

class MainScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        sceneContainer.onEvent(PacketEvent) {
            val packet = it.packet
            if (packet !is ServerClosedPacket) return@onEvent
            loadingMenu("서버와의 연결이 끊겼습니다") {
                launchNow { sceneContainer.changeTo<MainScene>() }
            }
        }
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