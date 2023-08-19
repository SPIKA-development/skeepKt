package ui

import korlibs.image.color.Colors
import korlibs.image.text.TextAlignment
import korlibs.korge.style.styles
import korlibs.korge.style.textAlignment
import korlibs.korge.style.textSize
import korlibs.korge.ui.uiContainer
import korlibs.korge.ui.uiMaterialLayer
import korlibs.korge.ui.uiText
import korlibs.korge.ui.uiVerticalStack
import korlibs.korge.view.*
import korlibs.korge.view.align.centerOn
import korlibs.korge.view.align.centerXOn
import korlibs.math.geom.RectCorners
import korlibs.math.geom.Size
import network.CreateRoom
import screen
import styler
import ui.custom.*
import util.ColorPalette
import util.transform

class NewCreateRoomMenuState {
    lateinit var createRoomMenu: Container
    val createRoomPadding = 30.75f
    val minAmount = 2
    val maxAmount = 12
    val range = maxAmount - minAmount + 1
    val blockHeight get() = screen.width / 15
    val blockSize get() = Size(500, 120)
    val inputSize get() = Size(screen.width / 3f, screen.width / 16f)
    lateinit var roomName: UITextInput
    lateinit var roomMaxPlayers: CustomUISlider
    val cellSize = Size(100, 100)
    val cellSelectorSize = Size(cellSize.height*range, cellSize.height)
}
fun newCreateRoomMenu(): Unit = screen.run { NewCreateRoomMenuState().run { container {
    createRoomMenu = this

    uiVerticalStack(adjustSize = false, padding = createRoomPadding*3) {
        styles(styler)
        customUiText("방 생성") {
            styles.textSize = styles.textSize * 1.25f
        }
        uiContainer(blockSize) {
            uiMaterialLayer(blockSize) {
                transform { size = blockSize }
                shadowColor = Colors.TRANSPARENT
                bgColor = ColorPalette.base
                borderColor = Colors.TRANSPARENT
                borderSize = createRoomPadding / 6
                radius = RectCorners(borderSize * 2)
            }.zIndex(1)
            customUiTextInput("방 이름", size = blockSize) {
                transform { size = blockSize }
            }.zIndex(2)
            transform { centerXOn(createRoomMenu) }
        }
        customUiText("최대 인원") {
            styles.textSize = styles.textSize * 1.2f
        }
        uiContainer(cellSelectorSize) {
            styles.textSize = styles.textSize * 0.7f
            repeat(range) {
                uiContainer(cellSize) cell@{
                    transform { size(cellSize).positionX(it*height) }
                    uiText("${it+minAmount}").transform { centerOn(this@cell) }
                }
            }
            roomMaxPlayers = customUiSlider(
                value = CreateRoom.defaultRoomMaxPlayers,
                min = minAmount,
                max = maxAmount,
                size = cellSelectorSize,
                step = 1
            ).transform { size = cellSelectorSize }
        }.transform { centerXOn(createRoomMenu) }
    }
}.transform { centerOn(screen) } } }