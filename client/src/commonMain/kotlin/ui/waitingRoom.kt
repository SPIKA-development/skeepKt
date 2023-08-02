package ui

import korlibs.korge.style.styles
import korlibs.korge.ui.uiContainer
import korlibs.korge.ui.uiText
import korlibs.korge.view.align.alignX
import korlibs.korge.view.align.alignY
import kotlinx.uuid.UUID
import network.ViewedRoom
import network.getRoomName
import scene.styler
import sceneContainer

suspend fun waitingRoom(room: UUID) {
    sceneContainer.uiContainer {
        styles(styler)
        uiText(getRoomName(room)) {

        }
            .alignY(root, 0.5, true)
            .alignX(root, 0.5, true)
    }
}