package ui

import korlibs.image.color.Colors
import korlibs.image.font.Font
import korlibs.image.text.TextAlignment
import korlibs.korge.style.styles
import korlibs.korge.style.textAlignment
import korlibs.korge.style.textFont
import korlibs.korge.ui.*
import korlibs.korge.view.Container
import korlibs.korge.view.align.alignY
import korlibs.korge.view.align.centerOn
import korlibs.korge.view.views
import korlibs.math.geom.Size
import org.koin.mp.KoinPlatform
import scene.styler
import ui.custom.customUiGridStack
import ui.custom.customUiScrollable

suspend fun Container.mainMenu() {
    val windowSize = Size(views().virtualWidth * 0.7, views().virtualHeight * 0.7)

    uiVerticalStack(adjustSize = false) {
        uiButton(size = Size(200f, windowSize.height/8f)) {
            uiText("방 생성").centerOn(this)
        }.alignY(this, 0.1, false).centerOn(this)
        uiSpacing()
    }.centerOn(this)
}