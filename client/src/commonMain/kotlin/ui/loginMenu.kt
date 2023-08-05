package ui

import khangul.HangulProcessor
import korlibs.event.Key
import korlibs.image.color.Colors
import korlibs.image.font.Font
import korlibs.image.text.TextAlignment
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.input.*
import korlibs.korge.style.styles
import korlibs.korge.style.textColor
import korlibs.korge.style.textFont
import korlibs.korge.style.textSize
import korlibs.korge.ui.*
import korlibs.korge.view.Container
import korlibs.korge.view.addTo
import korlibs.korge.view.align.alignY
import korlibs.korge.view.align.centerOn
import korlibs.korge.view.align.centerOnStage
import korlibs.korge.view.align.centerXOn
import korlibs.korge.view.position
import korlibs.korge.view.solidRect
import korlibs.math.geom.Size
import korlibs.math.log
import network.client
import network.username
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform
import scene.styler
import sceneContainer
import ui.custom.UITextInput
import ui.custom.customUiButton
import ui.custom.customUiText
import util.ColorPalette
import util.launchNow
import websocket.startWebSocket

@KorgeExperimental
suspend fun loginMenu(container: Container) {
    lateinit var inputText: UITextInput
    lateinit var warningText: UIText
    lateinit var loginMenu: Container
    loginMenu = container.uiContainer {
        styles(styler)
        val padding = 15.75f
        uiVerticalStack(adjustSize = false, padding = padding) {
            val title = customUiText("Skeep") {
                styles.textFont = KoinPlatform.getKoin().get<Font>(named("bold"))
                styles.textSize = styles.textSize * 1.25f
            }.centerXOn(this)
            val space = uiSpacing(size = Size(0f, title.size.height * 0.25f))
            val blockSize = Size(sceneContainer.width / 3.6f, sceneContainer.width / 23f)
            uiContainer(size = blockSize) {
                styles.textSize = styles.textSize * 0.95f
                materialInput("닉네임", padding, this,
                    border = Colors.TRANSPARENT, bg = ColorPalette.base
                ).apply {
                    inputText = input
                        input.mouse {
//                            onMove { materialLayer.borderColor = ColorPalette.hover }
                            onMoveOutside { materialLayer.borderColor = ColorPalette.base }
                        }
                    }
            }.centerXOn(this)
            val horizontalSize = Size(blockSize.width / 2 - padding / 2, blockSize.height)
            uiHorizontalStack(height = horizontalSize.height, adjustHeight = false, padding = padding) {
                uiSpacing(Size(0f, padding / 2))
                customUiButton(size = horizontalSize) {
                    uiMaterialLayer(size) {
                        shadowColor = Colors.TRANSPARENT
                        bgColor = ColorPalette.out
                        borderColor = ColorPalette.out
                        borderSize = padding / 4
                        this@customUiButton.mouse {
                            onMove { borderColor = ColorPalette.hover }
                            onMoveOutside { borderColor = ColorPalette.out }
                        }
                    }
                    uiText("입장 >").centerOn(this)
                    suspend fun join() {
                        val txt = inputText.text.trim()
                        if (txt.isEmpty()) {
                            warningText.text = "닉네임을 입력해주세요"
                            warningText.styles.textColor = Colors.PALEVIOLETRED
                            return
                        } else if (txt.length !in 3..16) {
                            warningText.text = "닉네임은 3글자 이상 16글자 이하여야 합니다"
                            warningText.styles.textColor = Colors.PALEVIOLETRED
                            return
                        }
                        warningText.text = "로그인 중..."
                        warningText.styles.textColor = ColorPalette.out
                        username = inputText.text.trim()
                        client()
                        loginMenu.removeFromParent()
                        launchNow { MainMenuState().mainMenu() }
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
        .alignY(container, 0.45, true)
}