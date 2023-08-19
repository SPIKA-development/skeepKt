package ui

import korlibs.event.Key
import korlibs.image.color.Colors
import korlibs.image.text.TextAlignment
import korlibs.korge.annotations.KorgeExperimental
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
import korlibs.korge.view.positionX
import korlibs.math.geom.Size
import network.*
import network.CreateRoomResultType.*
import styler
import screen
import ui.custom.*
import ui.custom.UITextInput
import username
import util.ColorPalette
import util.launchNow
import util.transform

class CreateRoomState {
    val createRoomPadding = 30.75f
    val minAmount = 2
    val maxAmount = 12
    val rangeSize = maxAmount - minAmount + 1
    val blockHeight get() = screen.width / 15
    val blockSize get() = Size(blockHeight * rangeSize, blockHeight)
    val inputSize get() = Size(screen.width / 3f, screen.width / 16f)
    lateinit var roomName: UITextInput
    lateinit var roomSize: CustomUISlider

}

@OptIn(KorgeExperimental::class)
fun Container.createRoomMenu(createRoom: CreateRoomState = CreateRoomState()): Unit = createRoom.run {
    lateinit var warningText: UIText
    lateinit var createRoomMenu: Container
    createRoomMenu = uiContainer(size) {
        styles(styler)
        uiVerticalStack(adjustSize = false, padding = createRoomPadding) {
            val title = customUiText("방 생성") {
                styles.textSize = styles.textSize * 1.25f
            }.centerXOn(this)
            uiVerticalStack(adjustSize = false, padding = createRoomPadding) {
                uiSpacing(size = Size(0f, title.size.height * 0.25f))
                    uiContainer(inputSize) {
                        materialInput(
                            "이름", createRoomPadding, this,
                            border = Colors.TRANSPARENT, bg = ColorPalette.base
                        ).input.apply {
                            roomName = this
                            text = "${username}의 방"
                            mouse {
//                            onMove { materialLayer.borderColor = ColorPalette.hover }
//                                onMoveOutside { materialLayer.borderColor = ColorPalette.base }
                            }
                        }
                    }.centerXOn(this)
                uiSpacing(blockSize.times(1))
                uiText("최대 인원") {
                    styles.textSize = styles.textSize * 1.2f
                }
                uiContainer(size = blockSize) {
                    styles.textSize = styles.textSize * 0.95f
                    styles.textAlignment = TextAlignment.MIDDLE_CENTER
                        repeat(rangeSize) {
                            uiContainer(Size(height, height)) {
                                uiText("${it+minAmount}").centerOn(this)
                                positionX(it*height)
                            }
                        }
                    roomSize = customUiSlider(
                        value = CreateRoom.defaultRoomMaxPlayers,
                        min = minAmount,
                        max = maxAmount,
                        size = size,
                        step = 1
                    )
                }.centerXOn(this)
            }
            val horizontalSize = Size(inputSize.width / 2 - createRoomPadding / 2, inputSize.height)
            uiHorizontalStack(height = horizontalSize.height, adjustHeight = false, padding = createRoomPadding) {
                uiSpacing(Size(0f, createRoomPadding / 2))
                customUiButton(size = horizontalSize) {
                    uiMaterialLayer(size) {
                        shadowColor = Colors.TRANSPARENT
                        bgColor = ColorPalette.base
                        borderColor = ColorPalette.base
                        borderSize = createRoomPadding / 4
                        this@customUiButton.mouse {
                            onMove { borderColor = ColorPalette.hover }
                            onMoveOutside { borderColor = ColorPalette.base }
                        }
                    }
                    uiText("생성 >").centerOn(this)
                    var joinOnce = false
                    suspend fun join() {
                        if (joinOnce) return
                        val txt = roomName.text.trim()
                        if (txt.isEmpty()) {
                            warningText.text = "방 이름을 입력해주세요"
                            warningText.styles.textColor = Colors.PALEVIOLETRED
                            return
                        }
                        warningText.text = "방 생성 중..."
                        warningText.styles.textColor = ColorPalette.out
                        joinOnce = true
                        val createRoom = CreateRoom(roomName.text,
                            roomSize.index, RoomMode.NORMAL)
                        val createRoomResult = createRoom(createRoom)
                        when (createRoomResult.type) {
                            NOT_ALlOWED_NAME -> {
                                warningText.text = "방 이름은 한글, 영문, 숫자만 가능합니다"
                                warningText.styles.textColor = Colors.PALEVIOLETRED
                                return
                            }
                            NOT_ALLOWED_MAX_PLAYERS_AMOUNT -> {
                                warningText.text = "방 이름은 3글자 이상 16글자 이하여야 합니다"
                                warningText.styles.textColor = Colors.PALEVIOLETRED
                                return
                            }
                            else -> {}
                        }
                        val room = createRoomResult.room
                        joinRoom(room)
                        createRoomMenu.removeFromParent()
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
    }.transform {
        centerOn(screen)
        .alignY(this@createRoomMenu, 0.5, true)
    }
}