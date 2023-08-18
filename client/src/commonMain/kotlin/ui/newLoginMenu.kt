package ui

import korlibs.event.Key
import korlibs.image.color.Colors
import korlibs.image.text.TextAlignment
import korlibs.korge.input.keys
import korlibs.korge.input.mouse
import korlibs.korge.input.onClick
import korlibs.korge.input.onMouseDragCloseable
import korlibs.korge.style.styles
import korlibs.korge.style.textAlignment
import korlibs.korge.style.textColor
import korlibs.korge.style.textSize
import korlibs.korge.ui.*
import korlibs.korge.view.Container
import korlibs.korge.view.align.*
import korlibs.korge.view.positionX
import korlibs.korge.view.zIndex
import korlibs.math.geom.RectCorners
import korlibs.math.geom.Size
import network.*
import styler
import screen
import ui.custom.UITextInput
import ui.custom.customUiButton
import ui.custom.customUiTextInput
import util.ColorPalette
import util.launchNow
import util.transform

class LoginMenuState {
    val loginMenuPadding = 40f
    val blockSize get() = Size(480, 75)
    val loginMenuSize get() = Size(550, 500)
    val loginButtonSize = Size(blockSize.width / 2 - loginMenuPadding / 2, blockSize.height)
    lateinit var input: UITextInput
    lateinit var warningText: UIText
    lateinit var loginMenu: Container
    lateinit var joinButton: Container
    var joinOnce = false
}

fun loginMenuView(loginMenuState: LoginMenuState = LoginMenuState()): Unit = screen.run { loginMenuState.run {
    uiContainer(loginMenuSize) loginMenu@{

        loginMenu = this
        styles(styler)
        uiVerticalStack(adjustSize = false, padding = loginMenuPadding) {
            uiSpacing(size = Size(0f, loginMenuPadding))
            uiText("스키프").transform { centerXOn(this@loginMenu) }
            uiContainer(blockSize) nickNameInput@{
                transform { centerXOn(this@loginMenu) }
                styles.textSize = styles.textSize * 0.95f
                styles.textAlignment = TextAlignment.MIDDLE_LEFT
                input = customUiTextInput("닉네임", size = size) {
                    text = " "
                }.zIndex(2).transform {
                    size = this@nickNameInput.size
                    controller.caretContainer.alignY(this, 0.75, false)
                    positionX(loginMenuPadding / 2)
                }
                uiMaterialLayer {
                    transform { size = input.size }
                    shadowColor = Colors.TRANSPARENT
                    bgColor = ColorPalette.base
                    borderColor = Colors.TRANSPARENT
                    borderSize = loginMenuPadding / 6
                    radius = RectCorners(borderSize * 2)
                }.zIndex(1)
            }
            customUiButton(size = loginButtonSize) {
                uiMaterialLayer(size) {
                    shadowColor = Colors.TRANSPARENT
                    bgColor = ColorPalette.base
                    borderColor = ColorPalette.base
                    borderSize = loginMenuPadding / 6
                    radius = RectCorners(borderSize*2)
                    this@customUiButton.mouse {
                        onMove { borderColor = ColorPalette.hover }
                        onMoveOutside { borderColor = ColorPalette.base }
                    }
                }
                joinButton = uiText(" 입장 >").transform { centerOn(this@customUiButton) }
                transform { centerXOn(this@loginMenu) }
            }
            uiSpacing(size = Size(0f, loginMenuPadding))
            warningText =uiText("") {
                styles(styler)
                styles.textSize = styles.textSize * 0.75f
                styles.textColor = ColorPalette.out
                transform { centerXOn(this@loginMenu) }
            }
            var joinOnce = false
            joinButton.apply {
                onClick { join() }
                onMouseDragCloseable { launchNow { join() } }
                keys { down(Key.ENTER) { join() } }
            }

        }
//        solidRect(size).transform { size = this@loginMenu.size }.alpha = 0.5f
    }.transform { centerXOn(screen).alignY(screen, 0.5, true) }
} }

suspend fun LoginMenuState.join() {
    if (joinOnce) return
    val txt = input.text.trim()
    if (txt.isEmpty()) {
        warningText.text = "닉네임을 입력해주세요"
        warningText.centerXOn(loginMenu)
        warningText.styles.textColor = Colors.PALEVIOLETRED
        return
    } else if (txt.length !in 3..16) {
        warningText.text = "닉네임은 3글자 이상 16글자 이하여야 합니다"
        warningText.centerXOn(loginMenu)
        warningText.styles.textColor = Colors.PALEVIOLETRED
        return
    } else if (!usernameRegex.containsMatchIn(txt)) {
        warningText.text = "닉네임은 한글, 영문, 숫자만 가능합니다"
        warningText.centerXOn(loginMenu)
        warningText.styles.textColor = Colors.PALEVIOLETRED
        return
    }
    warningText.text = "로그인 중..."
    warningText.centerXOn(loginMenu)
    warningText.styles.textColor = ColorPalette.out
    username = input.text.trim()
    joinOnce = true
    initializeClient()
    val login = login()
    if (login == LoginResultType.ALREADY_JOINED) {
        warningText.text = "입력하신 닉네임은 이미 사용중입니다"
        joinOnce = false
        return
    } else if (login == LoginResultType.SERVER_IS_NOT_AVAILABLE
        || runCatching { websocketClient() }
            .also { it.exceptionOrNull()?.printStackTrace() }.isFailure) {
        warningText.text = "죄송합니다, 지금은 인증 서버를 사용할 수 없습니다.\n나중에 다시 시도해 주세요."
        warningText.centerXOn(loginMenu)
        joinOnce = false
        return
    } else {
        loginMenu.removeFromParent()
        MainMenuState().mainMenu()
    }
}
