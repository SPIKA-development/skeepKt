package ui

import event.PacketEvent
import korlibs.datastructure.getExtra
import korlibs.datastructure.setExtra
import korlibs.event.Key
import korlibs.image.bitmap.*
import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.image.text.TextAlignment
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.input.keys
import korlibs.korge.input.onMouseDragCloseable
import korlibs.korge.input.onUp
import korlibs.korge.input.onUpAnywhere
import korlibs.korge.style.styles
import korlibs.korge.style.textAlignment
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.korge.view.align.alignXY
import korlibs.math.geom.Size
import kotlinx.uuid.UUID
import network.*
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform.getKoin
import scene.styler
import sceneContainer
import ui.custom.*
import util.ColorPalette
import util.launchNow
import websocket.getRoomName
import websocket.leaveRoom
import websocket.listPlayer
import websocket.sendToServer
import kotlin.math.max

class WaitingRoomState {
    val padding = 25f
    lateinit var profileSize: Size
    lateinit var profiles: Container
    lateinit var chats: Container
    lateinit var space: Container
    lateinit var scroll: CustomUIScrollable
}

@OptIn(KorgeExperimental::class)
suspend fun WaitingRoomState.waitingRoom(room: UUID) {
    lateinit var waitingRoom: View
    sceneContainer.uiContainer {
        waitingRoom = this
        val sidebarSize = Size(sceneContainer.width / 3.5f, sceneContainer.height)
        styles(styler)
        val belowElementHeight = sceneContainer.width / 25f
        val leaveButton = Size(belowElementHeight*1.75f, belowElementHeight)
        val inputBarSize = Size(sceneContainer.width - sidebarSize.width - padding*2 - leaveButton.width - padding*2, belowElementHeight)
        val titleSize = Size(sceneContainer.width - sidebarSize.width, belowElementHeight * 1.5f)
        val title = uiContainer(size = titleSize) {
            uiText(getRoomName(room)).centerYOn(this)
        }.position(padding, padding)

        uiContainer(Size(sidebarSize.width - padding, sidebarSize.height - padding)) {
            profileSize = Size(size.width, size.height/6f - padding)

            val stackPadding = padding / 2
            profiles = uiVerticalStack(adjustSize = false, padding = stackPadding)
            solidRect(size = Size(size.width, (profileSize.height+padding/2f)*6f- stackPadding), color = ColorPalette.base).zIndex(-1)
            positionX(sceneContainer.width - sidebarSize.width)
            positionY(padding)
        }
        customUiButton(size = leaveButton) {
            val back = solidRect(size, color = ColorPalette.out).centerOn(this)
            var isDone = false
            customUiText("나가기").centerOn(this)
            positionX(padding)
            positionY(sceneContainer.height - padding - size.height)
            onMouseDragCloseable {
                onUp up@{
                    if (isDone) return@up
                    isDone = true
                    leaveRoom(sessionUUID)
                    waitingRoom.removeFromParent()
                    MainMenuState().mainMenu()
                }
            }
        }

        val chatSize = Size(inputBarSize.width, sceneContainer.height - titleSize.height - inputBarSize.height - padding * 3)
        customUiScrollable(cache = false, size = chatSize) {
            scroll = it
            it.positionX(padding + leaveButton.width + padding)
            it.positionY(padding + titleSize.height + padding)
            it.backgroundColor = Colors.TRANSPARENT
            it.horizontal.view.visible = false
            it.vertical.view.visible = false
            it.scrollBarAlpha = 0f
            uiVerticalStack(width = size.width, padding = padding, adjustSize = false) {
                chats = this
                space = uiSpacing(Size(size.width, chatSize.height))
                styles(styler)
                styles {
                    textAlignment = TextAlignment.MIDDLE_LEFT
                }
                scroll.horizontal.view.visible = false
                scroll.scrollTopRatio = 1f
                onEvent(PacketEvent) {
                    val packet = it.packet
                    if (packet !is PlayerJoinPacket) return@onEvent
                    val username = packet.username
                    uiText("${username}이(가) 서버에 참여했습니다")
                    profile(username, profiles, profileSize)
                }
                onEvent(PacketEvent) {
                    val packet = it.packet
                    if (packet !is PlayerLeavePacket) return@onEvent
                    val username = packet.username
                    chat("${username}이(가) 서버를 떠났습니다")

                    profiles.removeChildrenIf { index, child -> child.getExtra("profile") == username }
                }
                onEvent(PacketEvent) { event ->
                    val packet = event.packet
                    if (packet !is ChatPacket) return@onEvent
                    val (username, message) = packet
                    chat("<${username}> $message")
                }
            }
        }
        uiContainer {
                val input = customUiTextInput(size = inputBarSize.minus(Size(padding/2, 0f))) {
                    text = " "
                    styles { textAlignment = TextAlignment.MIDDLE_LEFT }
                    controller.textView.alignment = TextAlignment.MIDDLE_LEFT
                    controller.caretContainer.alignY(this, 0.75, false)
                    positionX(this@waitingRoom.padding/2)
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
    listPlayer().forEach {
        profile(it.username, profiles, profileSize)
    }
}

fun WaitingRoomState.profile(name: String, container: Container, profileSize: Size) {
    container.uiContainer(Size(profileSize.width, profileSize.height)) {
//        solidRect(size, color = Colors.WHITE)
        setExtra("profile", name)
        val profileImageSize = Size(profileSize.height, profileSize.height)
        val imageBitmap = getKoin().get<Bitmap>(named("profile")).toBMP32().apply {
            updateColors {
                if (it != Colors.TRANSPARENT) {
                    val t = ColorPalette.hover
                    RGBA(t.r, t.g, t.b, it.a)
                } else it
            }
        }.slice()
        uiImage(size = Size(imageBitmap.width, imageBitmap.height), bitmap = imageBitmap) {
            scaleXY = profileImageSize.width / size.width
        }//}.centerYOn(this)
        uiText(name) { styles.textAlignment = TextAlignment.TOP_LEFT }
            .alignX(this, 0.5, true)
            .alignY(this, 0.15, true)

    }
}

fun WaitingRoomState.chat(chat: String) {
    val chat = chats.uiText(chat)
    space.height = max(0f, space.height - chat.height)
    scroll.scrollTopRatio = 1f
}