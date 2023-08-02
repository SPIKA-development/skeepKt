package ui

import korlibs.datastructure.get
import korlibs.datastructure.getExtra
import korlibs.datastructure.set
import korlibs.datastructure.setExtra
import korlibs.image.color.Colors
import korlibs.korge.input.mouse
import korlibs.korge.input.onClick
import korlibs.korge.style.styles
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.Point
import korlibs.math.geom.Size
import korlibs.time.DateTime
import korlibs.time.milliseconds
import network.ViewedRoom
import network.createRoom
import network.getViewedRooms
import scene.styler
import sceneContainer
import ui.custom.customUiButton
import ui.custom.customUiScrollable
import util.ColorPalette
import kotlin.math.PI

class MainMenuState {
    val elementRatio = 9
    val padding = 10f
    lateinit var rooms: Container
    lateinit var serverList: View
    lateinit var buttonCursor: View
    val buttonSize = Size(sceneContainer.width * 0.7, sceneContainer.height * 0.165)
    var clickElapsed = DateTime.now()
}

suspend fun MainMenuState.mainMenu() {
    serverList = sceneContainer.uiContainer {
        styles(styler)
        val bottom =
            solidRect(Size(sceneContainer.widthD, sceneContainer.heightD / elementRatio*1.75), color = ColorPalette.base)
                .zIndex(1)
                .position(.0, sceneContainer.heightD - sceneContainer.heightD / elementRatio*1.75)
        val top = solidRect(Size(sceneContainer.widthD, sceneContainer.heightD / elementRatio), color = ColorPalette.base)
            .zIndex(1)
        buttonCursor = uiMaterialLayer(buttonSize.plus(Size(padding/2, padding/2))) {
            visible(false)
            shadowColor = Colors.TRANSPARENT
            bgColor = Colors.TRANSPARENT
            borderColor = ColorPalette.text
            borderSize = padding/2
        }.centerXOn(this)
        uiContainer {
            customUiScrollable(
                Size(sceneContainer.widthD, sceneContainer.heightD - (top.heightD + bottom.heightD)),
                cache = false,
                barPosOffset = Point((sceneContainer.width - buttonSize.width)/2 - padding, 0)
            ) {
//                positionX(sceneContainer.width*0.5)
                it.backgroundColor = Colors.TRANSPARENT
                uiVerticalStack(adjustSize = false, padding = padding) {
                    uiSpacing(Size(1f, padding))
                    styles(styler)
                    rooms = this
                    getViewedRooms().forEach { room ->
                        room(room)
                    }
                }
            }.positionY(sceneContainer.height / elementRatio)
        }
        uiText("멀티플레이").centerOn(top).alignY(top, 0.75, true)
            .zIndex(1)
        uiHorizontalStack(height = top.height*0.75f, padding = padding*2) {
            val bottomButtonSize = Size(top.heightD * PI, .0)
            customUiButton(size = bottomButtonSize).bottomButton("방 생성").onClick {
                room(createRoom())
            }
            customUiButton(size = bottomButtonSize).bottomButton("새로고침").onClick {
                rooms.removeChildrenIf { index, child -> child.isRoom }
                getViewedRooms().forEach {
                    room(it)
                }
            }
        }.centerOn(bottom).zIndex(1)
    }
}

fun Container.bottomButton(text: String): Container {
    val baseColor = ColorPalette.out
    val rect = solidRect(size = size, color = baseColor).centerOn(this)
    mouse {
        onMove { rect.color = ColorPalette.hover }
        onMoveOutside { rect.color = baseColor }
    }
    uiText(text).centerOn(this)
    return this

}

var View.isRoom
    get() = getExtra("isRoom") !== null
    set(value) = setExtra("isRoom", value.takeIf { it })

fun MainMenuState.room(room: ViewedRoom) {
    rooms.customUiButton(size = buttonSize) {
        isRoom = true
        uiImage {

        }
        uiText("${room.name}    ${room.curPlayers}/${room.maxPlayers}") {
        }.centerOn(this)
        val thisButton = this
        mouse {
            this.onDown {
                val now = DateTime.now()
                if (buttonCursor.visible && now - clickElapsed < 250.milliseconds) {
                    serverList.visible = false
                    lateinit var loading: View
                    loading = sceneContainer.uiContainer {
                        uiText("서버에 연결 중...") { styles(styler) }
                            .centerOnStage()
                            .alignY(containerRoot, 0.4, true)

                        customUiButton(size = Size(sceneContainer.width*0.471f, sceneContainer.width/25)) {
                            styles(styler)
                            bottomButton("취소")
                        }
                            .centerXOnStage()
                            .alignY(containerRoot, 0.6, true)
                            .onClick {
                                serverList.visible = true
                                loading.removeFromParent()
                            }
                    }
                    return@onDown
                } else clickElapsed = now
                buttonCursor.visible = true
                buttonCursor.addTo(thisButton)
                buttonCursor.centerOn(thisButton)
            }
        }
    }.centerXOn(sceneContainer)
}