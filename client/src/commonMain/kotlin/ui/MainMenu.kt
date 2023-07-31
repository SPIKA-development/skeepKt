package ui

import korlibs.image.color.Colors
import korlibs.korge.input.mouse
import korlibs.korge.input.onClick
import korlibs.korge.style.styles
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.korge.view.filter.backdropFilter
import korlibs.math.geom.Point
import korlibs.math.geom.Size
import scene.styler
import sceneContainer
import ui.custom.clicked
import ui.custom.customUiButton
import ui.custom.customUiScrollable
import util.ColorPalette

suspend fun Container.mainMenu() {
    val windowSize = Size(views().virtualWidth * 0.7, views().virtualHeight * 0.7)
    val elementRatio = 9
    val padding = 10f
    val bottom =
        solidRect(Size(sceneContainer.widthD, sceneContainer.heightD / elementRatio*1.75), color = ColorPalette.base)
            .zIndex(1)
            .position(.0, sceneContainer.heightD - sceneContainer.heightD / elementRatio*1.75)
    val top = solidRect(Size(sceneContainer.widthD, sceneContainer.heightD / elementRatio), color = ColorPalette.base)
        .zIndex(1)
    uiContainer {
        val buttonSize = Size(sceneContainer.width * 0.625, sceneContainer.height * 0.15)
        lateinit var rooms: View
        val buttonCursor = uiMaterialLayer(buttonSize) {
            shadowColor = Colors.TRANSPARENT
            bgColor = Colors.TRANSPARENT
            borderColor = ColorPalette.text
            borderSize = 5f
        }.centerXOn(this)
        customUiScrollable(
            Size(sceneContainer.widthD * 0.7, sceneContainer.heightD - (top.heightD + bottom.heightD)), cache = false,
            barPosOffset = Point(400, 0)
        ) {
            it.backgroundColor = Colors.TRANSPARENT
            rooms = uiVerticalStack(adjustSize = false, padding = padding) {
                uiSpacing(Size(1f, padding))
                styles(styler)
                repeat(15) {
                    customUiButton(size = buttonSize) {
                        uiText("${it}번 방") {
                        }.centerOn(this)
                        bgColorSelected = ColorPalette.hover
                        bgColorSelected = ColorPalette.hover
                        bgColorOut = ColorPalette.hover
                        bgColorDisabled = ColorPalette.hover
                        val thisButton = this
                        mouse {
                            this.onDown {
                                buttonCursor.addTo(thisButton)
                                buttonCursor.centerOn(thisButton)
                            }
                        }
                    }.centerXOn(this)
//                        .alignX(this, -0.01, true)
                }
            }.centerOn(this)
        }.positionY(sceneContainer.height / elementRatio)
    }.centerXOnStage()


    uiHorizontalStack(height = top.height/2) {
        customUiButton(size = Size(top.height*5/3, 0f)) {
            uiText("방 생성").centerOn(this)
        }
    }.centerOn(bottom).zIndex(1)
}