package ui

import korlibs.image.color.Colors
import korlibs.image.text.HorizontalAlign
import korlibs.image.text.TextAlignment
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.style.styles
import korlibs.korge.style.textAlignment
import korlibs.korge.ui.uiButton
import korlibs.korge.ui.uiContainer
import korlibs.korge.ui.uiMaterialLayer
import korlibs.korge.ui.uiText
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.Size
import korlibs.math.geom.bezier.Bezier
import kotlinx.uuid.UUID
import network.ViewedRoom
import network.getRoomName
import scene.styler
import sceneContainer
import ui.custom.customUiButton
import ui.custom.customUiText
import ui.custom.customUiTextInput
import util.ColorPalette
import kotlin.math.abs

@OptIn(KorgeExperimental::class)
suspend fun waitingRoom(room: UUID) {
    sceneContainer.uiContainer {
        val padding = 25f
        val sidebarSize = Size(sceneContainer.width / 5f, sceneContainer.height)
        styles(styler)
        uiText(getRoomName(room))
            .alignY(root, 0.075, true)
        val belowElementHeight = sceneContainer.width / 25f
        val leaveButton = Size(belowElementHeight*1.75f, belowElementHeight)
        val inputBarSize = Size(sceneContainer.width - sidebarSize.width - padding*2 - leaveButton.width - padding*2, belowElementHeight)
        customUiButton(size = leaveButton) {
            val back = solidRect(size, color = ColorPalette.out).centerOn(this)
            customUiText("나가기").centerOn(this)
            positionX(padding)
            positionY(sceneContainer.height - padding - size.height)
        }
            uiContainer {
                val input = customUiTextInput(size = inputBarSize.minus(Size(padding/2, 0f))) {
                    styles { textAlignment = TextAlignment.MIDDLE_LEFT }
                    controller.textView.alignment = TextAlignment.MIDDLE_LEFT
                    controller.caretContainer.alignY(this, 0.75, false)
                    positionX(padding/2)
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