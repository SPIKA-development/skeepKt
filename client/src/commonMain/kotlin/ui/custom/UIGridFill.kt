package ui.custom

import korlibs.korge.ui.UIContainer
import korlibs.korge.view.Container
import korlibs.korge.view.addTo
import korlibs.korge.view.size
import korlibs.korge.view.xy
import korlibs.math.annotations.ViewDslMarker
import korlibs.math.geom.Size

inline fun Container.customUiGridStack(
    size: Size,
    cols: Int = 3,
    rows: Int = 3,
    paddingRatio: Float = 0f,
    block: @ViewDslMarker CustomUIGridStack.() -> Unit = {}
) = CustomUIGridStack(size, cols, rows, paddingRatio).addTo(this).apply(block)

open class CustomUIGridStack(
    val initialSize: Size, cols: Int = 3, rows: Int = 3, private val paddingRatio: Float
) : UIContainer(Size(initialSize.width, 0f)) {
    var cols: Int = cols
    var rows: Int = rows



    override fun relayout() {
        var y = 0f
        val elementHeight = initialSize.heightD / rows
        val elementWidth = widthD / cols
        forEachChildWithIndex { index, view ->
            val ex = index % cols
            val ey = index / cols
            view.xy(ex * elementWidth + paddingRatio/2f, ey * elementHeight + paddingRatio/2f)
            view.size(elementWidth-paddingRatio/2f, elementHeight-paddingRatio/2f)
            y += (elementHeight + paddingRatio/2).toFloat()
        }
        unscaledHeight = y/2
    }
}
