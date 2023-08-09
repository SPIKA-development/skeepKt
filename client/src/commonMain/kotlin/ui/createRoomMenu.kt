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
import korlibs.korge.ui.UIText
import korlibs.korge.view.Container
import korlibs.korge.view.addTo
import korlibs.korge.view.align.*
import korlibs.math.geom.Size
import network.*
import scene.styler
import sceneContainer
import ui.custom.*
import ui.custom.UITextInput
import util.ColorPalette
import util.launchNow

fun createRoomMenu(container: Container) {
    lateinit var inputText: UITextInput
    lateinit var warningText: UIText
    lateinit var createRoomMenu: Container
    createRoomMenu = container.uiContainer {
        styles(styler)
        val padding = 15.75f
        val minAmount = 2
        val maxAmount = 12
        val recommendedAmount = 6
        val rangeSize = maxAmount - minAmount -1
        val blockHeight = sceneContainer.width / 23f
        val blockSize = Size(blockHeight * rangeSize, blockHeight)
        val inputSize = Size(sceneContainer.width / 3.6f, sceneContainer.width / 23f)
        lateinit var roomName: MaterialInput
        lateinit var roomSize: CustomUISlider
        uiVerticalStack(adjustSize = false, padding = padding) {
            val title = customUiText("방 생성") {
                styles.textSize = styles.textSize * 1.25f
            }.centerXOn(this)
            uiVerticalStack(adjustSize = false, padding = padding) {
                uiSpacing(size = Size(0f, title.size.height * 0.25f))
                uiHorizontalStack {
                    uiContainer {
                        uiText("방 이름:") {
                            styles.textSize = styles.textSize * 1.2f
                        }.centerYOn(this)
                    }
                    uiContainer(inputSize) {
                        materialInput(
                            "이름", padding, this,
                            border = Colors.TRANSPARENT, bg = ColorPalette.base
                        ).apply {
                            roomName = this
                            input.text = "${username}의 방"
                            input.mouse {
//                            onMove { materialLayer.borderColor = ColorPalette.hover }
//                                onMoveOutside { materialLayer.borderColor = ColorPalette.base }
                            }
                        }
                    }
                }
                uiSpacing(blockSize)
                uiHorizontalStack(adjustHeight = true) {
                    uiContainer {
                        uiText("최대 인원:") {
                            styles.textSize = styles.textSize * 1.2f
                        }.centerYOn(this)
                    }
                    uiContainer(size = blockSize) {
                        styles.textSize = styles.textSize * 0.95f
                        styles.textAlignment = TextAlignment.MIDDLE_CENTER
                        uiHorizontalFill(size) {
                            (minAmount..maxAmount).forEach {
                                uiText("$it").centerYOn(this)
                            }
                        }
                        roomSize = customUiSlider(
                            value = recommendedAmount,
                            min = minAmount,
                            max = maxAmount,
                            size = size,
                            step = 1
                        )
                    }
                }
            }.alignX(this, -.3, false)
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
                        val txt = roomName.input.text.trim()
                        if (txt.isEmpty()) {
                            warningText.text = "방 이름을 입력해주세요"
                            warningText.styles.textColor = Colors.PALEVIOLETRED
                            return
                        } else if (txt.length !in 3..16) {
                            warningText.text = "방 이름은 3글자 이상 16글자 이하여야 합니다"
                            warningText.styles.textColor = Colors.PALEVIOLETRED
                            return
                        } else if (!usernameRegex.containsMatchIn(txt)) {
                            warningText.text = "방 이름은 한글, 영문, 숫자만 가능합니다"
                            warningText.styles.textColor = Colors.PALEVIOLETRED
                            return
                        }
                        warningText.text = "방 생성 중..."
                        warningText.styles.textColor = ColorPalette.out
                        joinOnce = true
                        createRoomMenu.removeFromParent()
                        val room = createRoom(CreateRoom(roomName.input.text, roomSize.index + minAmount)).uuid
                        joinRoom(room)
                        WaitingRoomState().waitingRoom(room)
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