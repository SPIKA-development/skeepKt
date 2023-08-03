package ui

import io.ktor.websocket.serialization.*
import korlibs.event.Key
import korlibs.image.color.Colors
import korlibs.image.text.HorizontalAlign
import korlibs.image.text.TextAlignment
import korlibs.io.lang.Cancellable
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.input.*
import korlibs.korge.style.styles
import korlibs.korge.style.textAlignment
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.Size
import korlibs.math.geom.bezier.Bezier
import kotlinx.uuid.UUID
import network.*
import scene.styler
import sceneContainer
import ui.custom.customUiButton
import ui.custom.customUiScrollable
import ui.custom.customUiText
import ui.custom.customUiTextInput
import util.ColorPalette
import util.launchNow
import kotlin.math.abs

@OptIn(KorgeExperimental::class)
suspend fun waitingRoom(room: UUID) {
    lateinit var waitingRoom: View
    sceneContainer.uiContainer {
        waitingRoom = this
        val padding = 25f
        val sidebarSize = Size(sceneContainer.width / 3.5f, sceneContainer.height)
        styles(styler)
        uiText(getRoomName(room)).position(padding, padding)
        val belowElementHeight = sceneContainer.width / 25f
        val leaveButton = Size(belowElementHeight*1.75f, belowElementHeight)
        val inputBarSize = Size(sceneContainer.width - sidebarSize.width - padding*2 - leaveButton.width - padding*2, belowElementHeight)
        customUiButton(size = leaveButton) {
            val back = solidRect(size, color = ColorPalette.out).centerOn(this)
            var isDone = false
            customUiText("나가기").centerOn(this).onMouseDragCloseable {
                onUpAnywhere {
                    if (isDone) return@onUpAnywhere
                    isDone = true
                    waitingRoom.removeFromParent()
                    MainMenuState().mainMenu()
                }
            }
            positionX(padding)
            positionY(sceneContainer.height - padding - size.height)
        }
        lateinit var chats: View
        customUiScrollable(size = inputBarSize) {
            it.backgroundColor = Colors.TRANSPARENT
            uiVerticalStack {
                chats = this
                uiSpacing(Size(1f, padding))
                styles(styler)
            }
        }
            uiContainer {
                val input = customUiTextInput(size = inputBarSize.minus(Size(padding/2, 0f))) {
                    text = " "
                    styles { textAlignment = TextAlignment.MIDDLE_LEFT }
                    controller.textView.alignment = TextAlignment.MIDDLE_LEFT
                    controller.caretContainer.alignY(this, 0.75, false)
                    positionX(padding/2)
                    keys {
                        down(Key.ENTER) {
                            send(ClientPacket.CHAT, text)
                            text = " "
                        }
                    }
                }.zIndex(2)
                uiMaterialLayer(input.size) {
                    shadowColor = Colors.TRANSPARENT
                    bgColor = Colors.TRANSPARENT
                    borderColor = ColorPalette.out
                    borderSize = padding / 4
                }.zIndex(1)
                solidRect(input.size, color = ColorPalette.base).zIndex(0)
                positionX(leaveButton.width + padding * 2)
                positionY(sceneContainer.height - padding - input.size.height)
            }
    }
}