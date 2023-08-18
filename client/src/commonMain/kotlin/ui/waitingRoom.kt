@file:OptIn(KorgeExperimental::class)
package ui

import event.PacketEvent
import korlibs.datastructure.getExtra
import korlibs.datastructure.setExtra
import korlibs.event.Key
import korlibs.image.bitmap.Bitmap
import korlibs.image.bitmap.slice
import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.image.text.TextAlignment
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.input.keys
import korlibs.korge.input.mouse
import korlibs.korge.input.onMouseDragCloseable
import korlibs.korge.input.onUp
import korlibs.korge.style.styles
import korlibs.korge.style.textAlignment
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.alignX
import korlibs.korge.view.align.alignY
import korlibs.korge.view.align.centerOn
import korlibs.korge.view.align.centerYOn
import korlibs.math.geom.RectCorners
import korlibs.math.geom.Size
import korlibs.time.seconds
import kotlinx.uuid.UUID
import network.*
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform.getKoin
import styler
import screen
import ui.custom.*
import ui.custom.UITextInput
import util.ColorPalette
import util.transform
import kotlin.math.max

class WaitingRoomState {
    val padding = 25f
    val stackPadding = padding / 2
    val profileSize get() = Size(sidebarContainerSize.width, sidebarContainerSize.height/6f - padding)
    lateinit var profiles: UIVerticalStack
    lateinit var chats: Container
    lateinit var space: Container
    lateinit var scroll: CustomUIScrollable
    val sidebarSize get() = Size(screen.width / 4.5f, screen.height)
    val belowElementHeight get() = screen.width / 25f
    val leaveButton get() = Size(belowElementHeight*1.75f, belowElementHeight)
    val inputBarSize get() = Size(screen.width - sidebarSize.width - padding*2 - leaveButton.width - padding*2, belowElementHeight)
    val titleSize get() = Size(screen.width - sidebarSize.width, belowElementHeight * 1.5f)
    val textInputSize get() = inputBarSize.minus(Size(padding / 2, 0f))
    val sidebarContainerSize get() = Size(sidebarSize.width - padding, sidebarSize.height - padding)
    val sidebarRectSize get() = Size(sidebarContainerSize.width, (profileSize.height + padding / 2f) * 6f - stackPadding)
    val chatSize get() = Size(inputBarSize.width, screen.height - titleSize.height - inputBarSize.height - padding * 3)
}

suspend fun WaitingRoomState.waitingRoom(room: UUID) {
    lateinit var waitingRoom: View
    screen.uiContainer {
        waitingRoom = this
        styles(styler)
        uiContainer(textInputSize) {
            val input = materialInput("채팅을 입력하세요...", padding, this).input
            input.apply {
                keys {
                    down(Key.ENTER) {
                        if (text.trim().isNotEmpty()) {
                            sendToServer(ClientPacket.CHAT, text)
                            text = " "
                        }
                    }
                }
            }
            solidRect(input.size, color = ColorPalette.base).zIndex(0)
                .transform { size(input.size) }
            transform {
                size(textInputSize)
                positionX(leaveButton.width + padding * 2)
                positionY(screen.height - padding - input.size.height)
            }
        }

        val title = uiContainer(size = titleSize) {
            uiText(getRoomName(room)).transform { centerYOn(this) }
        }.transform { size(titleSize).position(padding, padding) }

        uiContainer(sidebarContainerSize) {

            profiles = uiVerticalStack(adjustSize = true, padding = stackPadding)
            solidRect(size = sidebarRectSize, color = ColorPalette.base).zIndex(-1)
                .transform { size(sidebarRectSize) }
            transform {
                size(sidebarContainerSize)
                positionX(screen.width - sidebarSize.width)
                positionY(padding)
            }
        }
        customUiButton(size = leaveButton) leaveButton@{
            val back = solidRect(size, color = ColorPalette.out).transform { centerOn(this@leaveButton) }
            var isDone = false
            customUiText("나가기").transform { centerOn(this@leaveButton) }
            transform {
                size(leaveButton)
                positionX(padding)
                positionY(screen.height - padding - size.height)
            }
            onMouseDragCloseable {
                onUp up@{
                    if (isDone) return@up
                    isDone = true
                    requestLeaveRoom(sessionUUID)
                    waitingRoom.removeFromParent()
                    MainMenuState().mainMenu()
                }
            }
        }

        customUiScrollable(cache = true, disableMouseDrag = true, size = chatSize) {
            scroll = it
            it.transform {
                it.size(chatSize)
                it.positionX(padding + leaveButton.width + padding)
                it.positionY(padding + titleSize.height + padding)
            }
            it.backgroundColor = Colors.TRANSPARENT
            it.horizontal.view.visible = false
            it.vertical.view.visible = false
            it.scrollBarAlpha = 0f
            scroll.timeScrollBar = 0.seconds
            scroll.horizontal.view.visible = false
            scroll.scrollTopRatio = 1f
            uiVerticalStack(width = chatSize.width, padding = padding, adjustSize = false) {
                transform {
                    width = chatSize.width
                }
                chats = this
                space = uiSpacing(chatSize).transform { size(chatSize) }
                styles(styler)
                styles {
                    textAlignment = TextAlignment.MIDDLE_LEFT
                }
                onEvent(PacketEvent) {
                    val packet = it.packet
                    if (packet !is PlayerJoinPacket) return@onEvent
                    val username = packet.username
                    if (profiles.children.any { it.getExtra("profile") == username }) return@onEvent
                    chat("${username}이(가) 서버에 참여했습니다")
                    profileView(username, profiles)
                }
                onEvent(PacketEvent) {
                    val packet = it.packet
                    if (packet !is PlayerLeavePacket) return@onEvent
                    val username = packet.username
                    chat("${username}이(가) 서버를 떠났습니다")
                    profiles.removeChildrenIf { _, child -> child.getExtra("profile") == username }
                    profiles.relayout()
                }
                onEvent(PacketEvent) { event ->
                    val packet = event.packet
                    if (packet !is ChatPacket) return@onEvent
                    val (username, message) = packet
                    chat("<${username}> $message")
                }
            }
        }
    }
    listPlayer().forEach {
        profileView(it.username, profiles)
    }
}

fun WaitingRoomState.profileView(name: String, container: Container) {
    container.uiContainer(profileSize) profile@{
        transform { size(profileSize) }
//        solidRect(size, color = Colors.WHITE)
        setExtra("profile", name)
        val imageBitmap = getKoin().get<Bitmap>(named("profile")).toBMP32().apply {
            updateColors {
                if (it != Colors.TRANSPARENT) {
                    val t = ColorPalette.hover
                    RGBA(t.r, t.g, t.b, it.a)
                } else it
            }
        }.slice()
        uiImage(size = Size(imageBitmap.width, imageBitmap.height), bitmap = imageBitmap) {
            transform { scaleXY = profileSize.width / size.width }
        }//}.centerYOn(this)
        uiText(name) { styles.textAlignment = TextAlignment.MIDDLE_LEFT }.transform {
            alignX(this@profile, 0.85, false)
            alignY(this@profile, 0.15, true)
        }
    }
}

fun WaitingRoomState.chat(chat: String) {
    val chat = chats.uiText(chat)
    space.transform {
        height = max(0f, space.height - chat.height)
    }
    scroll.scrollTopRatio = 1f
}

data class MaterialInput(
    val input: UITextInput,
    val materialLayer: UIMaterialLayer
)
fun materialInput(hint: String, padding: Float, container: Container,
                  border: RGBA = ColorPalette.out,
                  bg: RGBA = Colors.TRANSPARENT,
): MaterialInput {
    container.styles.textAlignment = TextAlignment.MIDDLE_LEFT
    val input = container.customUiTextInput(hint, size = container.size) {
        text = " "
        transform {
            size = container.size
            controller.caretContainer.alignY(this, 0.75, false)
            positionX(padding / 2)
        }
    }.zIndex(2)
    val material = container.uiMaterialLayer {
        transform {
            size = input.size
        }
        shadowColor = Colors.TRANSPARENT
        bgColor = bg
        borderColor = border
        borderSize = padding / 6
        radius = RectCorners(borderSize*2)
    }.zIndex(1)
    return MaterialInput(input, material)
}

data class MaterialButton(val button: CustomUIButton, val materialLayer: UIMaterialLayer)
fun materialButton(text: String, container: Container,
                  border: RGBA = ColorPalette.hover,
                  bg: RGBA = ColorPalette.out,
): MaterialButton {
    container.styles.textAlignment = TextAlignment.MIDDLE_CENTER
    val button = container.customUiButton(size = container.size)
        .zIndex(2).transform { size = container.size }
    val material = container.uiMaterialLayer(button.size) {
        transform { size = button.size }
        shadowColor = Colors.TRANSPARENT
        bgColor = bg
//        borderColor = Colors.ba
//        borderSize = size.height / 15f
        radius = RectCorners(borderSize*2)
    }.zIndex(1)
    container.apply {
        uiText(text).transform { centerOn(container) }.zIndex(2)
        mouse {
            onMove { material.bgColor = border }
            onMoveOutside { material.bgColor = bg }
        }
    }
    return MaterialButton(button, material)
}