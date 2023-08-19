package newui

import d_card
import korlibs.image.bitmap.resized
import korlibs.image.bitmap.resizedUpTo
import korlibs.image.bitmap.slice
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.style.styles
import korlibs.korge.ui.uiContainer
import korlibs.korge.ui.uiHorizontalStack
import korlibs.korge.ui.uiImage
import korlibs.korge.ui.uiVerticalStack
import korlibs.korge.view.*
import korlibs.korge.view.align.centerOn
import korlibs.korge.view.align.centerXOn
import korlibs.korge.view.align.centerYOn
import korlibs.math.geom.Size
import screen
import styler
import ui.custom.customUiText
import util.transform

class MainViewState {
    val mainViewPadding = 20f
    val cardHorizontalPadding = 350f
}
fun mainView(root: Container = screen): Unit = MainViewState().run {
    root.uiContainer(root.size) mainMenu@{
        uiVerticalStack(width = this@mainMenu.width, adjustSize = false, padding = mainViewPadding*2) vert@{
            styles(styler)
            customUiText("Skeep").centerXOn(this@vert)
            uiHorizontalStack(adjustHeight = false, padding = cardHorizontalPadding) {
                val playCard = uiContainer {
                    uiImage(Size(d_card.width, d_card.height), d_card.slice()) {
                        scale(0.5, 0.5)
                    }
                }
                val userCard = uiContainer {
                    uiImage(Size(d_card.width, d_card.height), d_card.slice()) {
                        scale(0.5, 0.5)
                    }
                }
                val settingCard = uiContainer {
                    uiImage(Size(d_card.width, d_card.height), d_card.slice()) {
                        scale(0.5, 0.5)
                    }
                }
            }.centerXOn(this@vert)
        }.transform { width = this@mainMenu.width; centerOn(this@mainMenu) }
    }.transform { size(root.size).centerOn(root) }

}