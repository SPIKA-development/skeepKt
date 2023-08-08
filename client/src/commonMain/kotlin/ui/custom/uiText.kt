package ui.custom

import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.korge.input.mouse
import korlibs.korge.render.RenderContext
import korlibs.korge.style.*
import korlibs.korge.ui.UIView
import korlibs.korge.ui.uiObservable
import korlibs.korge.view.*
import korlibs.math.geom.Point
import korlibs.math.geom.Rectangle
import korlibs.math.geom.Size

// @TODO: Replace with TextBlock
inline fun Container.customUiText(
        text: String,
        size: Size = UIText.DEFAULT_SIZE,
        block: @ViewDslMarker UIText.() -> Unit = {}
): UIText = UIText(text, size).addTo(this).apply(block)

class UIText(
        text: String,
        size: Size = DEFAULT_SIZE,
) : UIView(size) {
    companion object {
        val DEFAULT_SIZE = Size(128, 18)
    }
    protected var bover by uiObservable(false) { updateState() }
    protected var bpressing by uiObservable(false) { updateState() }

    private val background = solidRect(size, Colors.TRANSPARENT)
    val textView = text(text, alignment = styles.textAlignment)
    var bgcolor: RGBA = Colors.TRANSPARENT

    var text: String by textView::text

    init {
        mouse {
            onOver {
                simulateOver()
            }
            onOut {
                simulateOut()
            }
            onDown {
                simulateDown()
            }
            onUpAnywhere {
                simulateUp()
            }
        }
    }

    fun simulateOver() {
        bover = true
    }

    fun simulateOut() {
        bover = false
    }

    fun simulatePressing(value: Boolean) {
        bpressing = value
    }

    fun simulateDown() {
        bpressing = true
    }

    fun simulateUp() {
        bpressing = false
    }

    private var textBounds = Rectangle()

    override fun renderInternal(ctx: RenderContext) {
        background.visible = bgcolor.a != 0
        background.colorMul = bgcolor
        textBounds = Rectangle(Point.ZERO, unscaledSize)
        textView.setFormat(face = styles.textFont, size = styles.textSize.toInt(), color = styles.textColor, align = styles.textAlignment)
        textView.setTextBounds(textBounds.immutable)
        //background.size(width, height)
        textView.text = text
        super.renderInternal(ctx)
    }

    override fun updateState() {
        super.updateState()
    }
}
