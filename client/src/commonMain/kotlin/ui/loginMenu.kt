package ui

import korlibs.image.color.Colors
import korlibs.image.font.Font
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.input.mouse
import korlibs.korge.input.onMove
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
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform
import scene.styler
import sceneContainer
import ui.custom.customUiButton
import ui.custom.customUiText
import util.ColorPalette

@KorgeExperimental
fun loginMenu(container: Container) {
    container.uiContainer() {
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
                materialInput("닉네임", padding, this, border = Colors.TRANSPARENT, bg = ColorPalette.base)
                    .apply {
                        input.mouse {
//                            onMove { materialLayer.borderColor = ColorPalette.hover }
                            onMoveOutside { materialLayer.borderColor = ColorPalette.base }
                        }
                    }
            }.centerXOn(this)
            uiContainer(size = blockSize) {
                styles.textSize = styles.textSize * 0.95f
                materialInput("비밀번호", padding, this, border = Colors.TRANSPARENT, bg = ColorPalette.base)
                    .apply {
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
                    uiText("게스트 >").centerOn(this)
                }
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
                    uiText("로그인 >").centerOn(this)
                }
            }.centerXOn(this)
            uiSpacing(size = Size(0f, space.size.height / 16f))
            val guestModeText = uiText("계정 없이 플레이하실 건가요?") {
                styles.textSize = styles.textSize * 0.75f
                mouse {
                    onMove { styles.textColor = ColorPalette.text }
                    onMoveOutside { styles.textColor = ColorPalette.out }
                }
            }.centerXOn(this)
            val guestModeTextContainer = uiContainer(guestModeText.size.times(3)) {
            }.centerXOn(this)
            guestModeText.removeFromParent()
            guestModeText.addTo(guestModeTextContainer).centerOn(guestModeTextContainer)
        }.centerOn(this)
    }.centerOnStage()
        .alignY(container, 0.45, true)
}