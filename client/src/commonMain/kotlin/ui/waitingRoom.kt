package ui

import event.PacketEvent
import korlibs.event.Key
import korlibs.image.color.Colors
import korlibs.image.text.TextAlignment
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.input.*
import korlibs.korge.style.styles
import korlibs.korge.style.textAlignment
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.Size
import kotlinx.uuid.UUID
import network.*
import scene.styler
import sceneContainer
import ui.custom.*
import util.ColorPalette
import websocket.sendToServer
import kotlin.math.max

@OptIn(KorgeExperimental::class)
suspend fun waitingRoom(room: UUID) {
    lateinit var waitingRoom: View
    sceneContainer.uiContainer {
        waitingRoom = this
        val padding = 25f
        val sidebarSize = Size(sceneContainer.width / 3.5f, sceneContainer.height)
        styles(styler)
        val belowElementHeight = sceneContainer.width / 25f
        val leaveButton = Size(belowElementHeight*1.75f, belowElementHeight)
        val inputBarSize = Size(sceneContainer.width - sidebarSize.width - padding*2 - leaveButton.width - padding*2, belowElementHeight)
        val titleSize = Size(sceneContainer.width - sidebarSize.width, belowElementHeight * 1.5f)
        val title = uiContainer(size = titleSize) {
            uiText(getRoomName(room)).centerYOn(this)
        }.position(padding, padding)
        customUiButton(size = leaveButton) {
            val back = solidRect(size, color = ColorPalette.out).centerOn(this)
            var isDone = false
            customUiText("나가기").centerOn(this).onMouseDragCloseable {
                onUpAnywhere {
                    if (isDone) return@onUpAnywhere
                    isDone = true
                    leaveRoom(sessionUUID)
                    waitingRoom.removeFromParent()
                    MainMenuState().mainMenu()
                }
            }
            positionX(padding)
            positionY(sceneContainer.height - padding - size.height)
        }
        lateinit var chats: View
        lateinit var scroll: CustomUIScrollable
        val chatSize = Size(inputBarSize.width, sceneContainer.height - titleSize.height - inputBarSize.height - padding * 3)
        customUiScrollable(cache = false, size = chatSize) {
            scroll = it
            it.positionX(padding + leaveButton.width + padding)
            it.positionY(padding + titleSize.height + padding)
            it.backgroundColor = Colors.TRANSPARENT
            lateinit var space: View
            uiVerticalStack(width = size.width, padding = padding, adjustSize = false) {
                chats = this
                space = uiSpacing(Size(size.width, chatSize.height))
                styles(styler)
                styles {
                    textAlignment = TextAlignment.MIDDLE_LEFT
                }
                scroll.scrollBarAlpha = 0f
                scroll.horizontal.view.visible = false
                scroll.scrollTopRatio = 1f
                onEvent(PacketEvent) { event ->
                    println("chat")
                    val packet = event.packet
                    if (packet !is ChatPacket) return@onEvent
                    val (username, message) = packet
                    val chat = uiText("<$username> $message")
                    space.height = max(0f, space.height - chat.height)
                    scroll.scrollTopRatio = 1f
                }
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
                            if (text.trim().isNotEmpty()) {
                                sendToServer(ClientPacket.CHAT, text)
                                text = " "
                            }
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