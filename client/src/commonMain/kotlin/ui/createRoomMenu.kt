package ui

import korlibs.event.Key
import korlibs.image.color.Colors
import korlibs.korge.input.keys
import korlibs.korge.input.mouse
import korlibs.korge.input.onClick
import korlibs.korge.input.onMouseDragCloseable
import korlibs.korge.style.styles
import korlibs.korge.style.textColor
import korlibs.korge.style.textSize
import korlibs.korge.ui.*
import korlibs.korge.view.Container
import korlibs.korge.view.addTo
import korlibs.korge.view.align.*
import korlibs.math.geom.Size
import network.*
import scene.styler
import sceneContainer
import ui.custom.UITextInput
import ui.custom.customUiButton
import ui.custom.customUiSlider
import ui.custom.customUiText
import util.ColorPalette
import util.launchNow

fun createRoomMenu(container: Container) {
    lateinit var inputText: UITextInput
    lateinit var warningText: UIText
    lateinit var loginMenu: Container
    loginMenu = container.uiContainer {
        styles(styler)
        val padding = 15.75f
        uiVerticalStack(adjustSize = false, padding = padding) {
            val title = customUiText("방 생성") {
                styles.textSize = styles.textSize * 1.25f
            }.centerXOn(this)
            uiSpacing(size = Size(0f, title.size.height * 0.25f))
            val blockSize = Size(sceneContainer.width / 3.6f, sceneContainer.width / 23f)
            uiContainer(size = blockSize) {
                styles.textSize = styles.textSize * 0.95f
                uiText("1").centerYOn(this)
                customUiSlider(value = 50f, min = 2f, max = 6f, size = size).centerOn(this)
            }.centerXOn(this)
            val horizontalSize = Size(blockSize.width / 2 - padding / 2, blockSize.height)
            uiHorizontalStack(height = horizontalSize.height, adjustHeight = false, padding = padding) {
                uiSpacing(Size(0f, padding / 2))
                customUiButton(size = horizontalSize) {
                    uiMaterialLayer(size) {
                        shadowColor = Colors.TRANSPARENT
                        bgColor = ColorPalette.base
                        borderColor = ColorPalette.base
                        borderSize = padding / 4
                        this@customUiButton.mouse {
                            onMove { borderColor = ColorPalette.hover }
                            onMoveOutside { borderColor = ColorPalette.base }
                        }
                    }
                    uiText("생성 >").centerOn(this)
                    var joinOnce = false
                    suspend fun join() {
                        if (joinOnce) return
                        val txt = inputText.text.trim()
                        if (txt.isEmpty()) {
                            warningText.text = "닉네임을 입력해주세요"
                            warningText.styles.textColor = Colors.PALEVIOLETRED
                            return
                        } else if (txt.length !in 3..16) {
                            warningText.text = "닉네임은 3글자 이상 16글자 이하여야 합니다"
                            warningText.styles.textColor = Colors.PALEVIOLETRED
                            return
                        } else if (!usernameRegex.containsMatchIn(txt)) {
                            warningText.text = "닉네임은 한글, 영문, 숫자만 가능합니다"
                            warningText.styles.textColor = Colors.PALEVIOLETRED
                            return
                        }
                        warningText.text = "로그인 중..."
                        warningText.styles.textColor = ColorPalette.out
                        username = inputText.text.trim()
                        joinOnce = true
                        val login = login()
                        if (login == LoginResultType.ALREADY_JOINED) {
                            warningText.text = "입력하신 닉네임은 이미 사용중입니다"
                            joinOnce = false
                            return
                        } else if (login == LoginResultType.SERVER_IS_NOT_AVAILABLE
                            || runCatching { websocketClient() }
                                .also { it.exceptionOrNull()?.printStackTrace() }.isFailure) {
                            warningText.text = "죄송합니다, 지금은 인증 서버를 사용할 수 없습니다. 나중에 다시 시도해 주세요."
                            joinOnce = false
                            return
                        } else {
                            loginMenu.removeFromParent()
                            MainMenuState().mainMenu()
                        }
                    }
                    onClick { join() }
                    onMouseDragCloseable { launchNow { join() } }
                    keys { down(Key.ENTER) { join() } }

                }
            }.centerXOn(this)
//            uiSpacing(size = Size(0f, space.size.height / 16f))
            val guestModeText = uiText("") {
                styles.textSize = styles.textSize * 0.75f
                styles.textColor = ColorPalette.out
            }.centerXOn(this)
            warningText = guestModeText
            val guestModeTextContainer = uiContainer(guestModeText.size.times(3)) {
            }.centerXOn(this)
            guestModeText.removeFromParent()
            guestModeText.addTo(guestModeTextContainer).centerOn(guestModeTextContainer)
        }.centerOn(this)
    }.centerOnStage()
        .alignY(container, 0.5, true)
}