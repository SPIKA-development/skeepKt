package ui

import io.ktor.http.*
import korlibs.datastructure.getExtra
import korlibs.datastructure.setExtra
import korlibs.image.bitmap.BmpSlice
import korlibs.image.color.Colors
import korlibs.korge.input.mouse
import korlibs.korge.input.onClick
import korlibs.korge.render.SDFShaders.pow
import korlibs.korge.style.styles
import korlibs.korge.time.timers
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.time.DateTime
import korlibs.time.milliseconds
import korlibs.time.seconds
import logo
import network.*
import scene
import styler
import screen
import screen
import ui.custom.customUiButton
import ui.custom.customUiScrollable
import ui.custom.customUiText
import util.ColorPalette
import util.transform
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

class MainMenuState {
    val elementRatio = 9
    val padding = 20f
    lateinit var rooms: Container
    lateinit var serverList: View
    lateinit var buttonCursor: View
    val buttonSize get() = Size(screen.width * 0.7, screen.height * 0.165)
    var clickElapsed = DateTime.now()
    lateinit var loading: View
    val bottomSize get() = Size(screen.widthD, screen.heightD / elementRatio * 1.75)
    val topSize get() = Size(screen.widthD, screen.heightD / elementRatio)
    val buttonCursorSize get() = buttonSize.plus(Size(padding / 2, padding / 2))
    val scrollableSize get() = Size(screen.widthD, screen.heightD - (topSize.heightD + bottomSize.heightD))
    val buttonsHorizontalSize get() = topSize.height - padding.pow((if (screen.width == scene.width)
        scene.height/screen.width else screen.width/scene.height)/5)
    val bottomButtonSize get() = Size(topSize.heightD * PI, buttonsHorizontalSize.toDouble())
    val logoSize get() = Size(buttonSize.height - padding, buttonSize.height - padding)

}

suspend fun MainMenuState.mainMenu() {

    screen.uiContainer mainMenu@{
        serverList = this
        styles(styler)
        val bottom =
            solidRect(bottomSize, color = ColorPalette.base).transform {
                size(bottomSize).position(.0, screen.heightD - screen.heightD / elementRatio*1.75)
            }.zIndex(1)
        val top = solidRect(topSize, color = ColorPalette.base).zIndex(1)
            .transform { size(topSize) }
        buttonCursor = uiMaterialLayer(buttonCursorSize) {
            visible(false)
            shadowColor = Colors.TRANSPARENT
            bgColor = Colors.TRANSPARENT
            borderColor = ColorPalette.text
            borderSize = padding/2
            radius = RectCorners(borderSize*2)
        }.transform { size(buttonCursorSize).centerXOn(serverList) }
        uiContainer {
            customUiScrollable(
                scrollableSize,
                cache = false,
                barPosOffset = Point((screen.width - buttonSize.width)/2 - padding, 0)
            ) {
//                positionX(screenContainer.width*0.5)
                it.backgroundColor = Colors.TRANSPARENT
                uiVerticalStack(adjustSize = false, padding = padding) {
                    uiVerticalStack(adjustSize = false, padding = padding) {
                        uiSpacing(Size(1f, padding))
                        styles(styler)
                        rooms = this
                    }
                    styles(styler)
                    var i = 0
                    val icons = listOf("0 o o", "o 0 o", "o o 0", "o 0 o")
                    customUiText("") {
                        transform { centerXOn(screen) }
                        timers.intervalAndNow(0.15.seconds) {
                            text = icons[i]
                            if (i < icons.size - 1) i++
                            else i = 0
                        }
                    }
                }
            }.transform { size(scrollableSize).positionY(screen.height / elementRatio) }
        }
        uiText("멀티플레이").transform { centerOn(top).alignY(top, 0.75, true) }.zIndex(2)
        uiHorizontalStack(height = buttonsHorizontalSize, padding = padding*2) {
            materialButton("방 생성", uiContainer(bottomButtonSize) {
                mouse.onClick {
                    serverList.removeFromParent()
//                    val room = createRoom(CreateRoom("asdf", 6))
//                    joinRoom(room.uuid)
//                    loading.removeFromParent()
//                    WaitingRoomState().waitingRoom(room.uuid)
                    screen.createRoomMenu()
                }
            }.transform { size(bottomButtonSize) })
            materialButton("새로고침", uiContainer(bottomButtonSize) {
                rooms.removeChildrenIf { index, child -> child.isRoom }
                loadRooms()
            }.transform { size(bottomButtonSize) })
//            uiContainer(size = bottomButtonSize).bottomButton("방 생성")
//                    joinRoom(room.uuid)
//                    loading.removeFromParent()
//                    WaitingRoomState().waitingRoom(room.uuid)
//            }
        }.transform { size(size.width, buttonsHorizontalSize).centerOn(bottom).zIndex(2) }
    }
}

fun Container.bottomButton(text: String): Container {
    val baseColor = ColorPalette.out
    val rect = solidRect(size = size, color = baseColor)
        .transform { size(this@bottomButton.size).centerOn(this@bottomButton) }
    mouse {
        onMove { rect.color = ColorPalette.hover }
        onMoveOutside { rect.color = baseColor }
    }
    uiText(text).transform { centerOn(this) }
    return this
}

var View.isRoom
    get() = getExtra("isRoom") !== null
    set(value) = setExtra("isRoom", value.takeIf { it })

fun MainMenuState.room(room: ViewedRoom) {
    rooms.customUiButton(size = buttonSize) button@{
        transform { size(buttonSize) }
        isRoom = true
        val logo = logo
        uiImage(size = logoSize, bitmap = logo, scaleMode = ScaleMode.FIT).transform {
            size(logoSize)
                .centerYOn(this@button)
                .alignX(this@button, 0.025, true)
                .alignY(this@button, 0.9, true)
        }
        uiText(room.name).transform {
            centerOn(this@button)
                .alignX(this@button, 0.2, true)
                .alignY(this@button, 0.175, true)
        }

        uiText("${room.curPlayers}/${room.maxPlayers}").transform {
            centerOn(this@button)
            .alignX(this@button, 1.025, true)
            .alignY(this@button, 0.175, true)
        }
        val thisButton = this
        mouse {
            this.onDown {
                val now = DateTime.now()
                if (buttonCursor.visible && now - clickElapsed < 250.milliseconds) {
                    serverList.visible = false
                    screen.uiContainer {
                        loading = this
                        val text = loadingMenu("서버에 연결 중...", "취소") {
                            loading.removeFromParent()
                            serverList.visible = true
                        }
                        when (joinRoom(room.uuid)) {
                            HttpStatusCode.OK -> {
                                loading.removeFromParent()
                                serverList.removeFromParent()
                                WaitingRoomState().waitingRoom(room.uuid)
                            }

                            HttpStatusCode.NotFound -> {
                                loading.removeFromParent()
                                loadingMenu("서버에 연결할 수 없습니다", "서버 목록으로 돌아가기") {

                                }
                            }

                            else -> {
                                loading.removeFromParent()
                                loadingMenu("서버 인원이 꽉 찼습니다!", "서버 목록으로 돌아가기") {

                                }
                            }
                        }
                    }
                    return@onDown
                } else clickElapsed = now
                buttonCursor.visible = true
                buttonCursor.addTo(thisButton)
                buttonCursor.centerOn(thisButton)
            }
        }
    }.transform { centerXOn(screen) }
}

val loadingMenuButtonSize get() = Size(screen.width * 0.471f, screen.height / 16)
fun Container.loadingMenu(text: String, buttonText: String? = null, onClick: () -> Unit): UIText {
    val text = uiText(text) {
        styles(styler)
        transform { centerOn(screen).alignY(screen, 0.4, true) }
    }
    val button =
        customUiButton(size = loadingMenuButtonSize) {
            styles(styler)
            if (buttonText !== null) bottomButton(buttonText).centerOn(this)
            transform { size(loadingMenuButtonSize).centerXOn(screen).alignY(screen, 0.6, true) }
            onClick { onClick.invoke() }
        }
    return text
}

suspend fun MainMenuState.loadRooms() {
    val viewedRooms = getViewedRooms()
//    listLoading.removeFromParent()
    if (viewedRooms.isEmpty()) {
        rooms.uiText("")
    } else viewedRooms.forEach {
        room(it)
    }
}

