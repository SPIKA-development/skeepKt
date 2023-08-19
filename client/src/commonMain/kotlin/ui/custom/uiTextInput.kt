package ui.custom

import korlibs.event.ISoftKeyboardConfig
import korlibs.event.SoftKeyboardConfig
import korlibs.image.font.Font
import korlibs.io.async.Signal
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.style.ViewStyle
import korlibs.korge.style.ViewStyles
import korlibs.korge.style.styles
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.alignX
import korlibs.korge.view.align.alignY
import korlibs.korge.view.align.centerYOn
import korlibs.math.geom.Margin
import korlibs.math.geom.Rectangle
import korlibs.math.geom.Size
import screen
import util.transform

inline fun Container.customUiTextInput(
    hint: String,
    initialText: String = " ",
    size: Size = Size(128, 24),
    block: @ViewDslMarker UITextInput.() -> Unit = {}
): UITextInput = UITextInput(parent = this, hint, initialText, size)
    .addTo(this).also { block(it) }

/**
 * Simple Single Line Text Input
 */
class UITextInput(parent: View, hint: String, initialText: String = " ", size: Size = Size(128, 24)) :
    UIView(size),
    //UIFocusable,
    ISoftKeyboardConfig by SoftKeyboardConfig() {

    //private val bg = ninePatch(NinePatchBmpSlice.createSimple(Bitmap32(3, 3) { x, y -> if (x == 1 && y == 1) Colors.WHITE else Colors.BLACK }.slice(), 1, 1, 2, 2), width, height).also { it.smoothing = false }
    private val bg = renderableView(size) {
        styles.uiTextInputBackgroundRender.apply {
            render()
        }
    }
    var skin by bg::viewRenderer
    private val container = clipContainer(Size.ZERO)
    //private val container = fixedSizeContainer(width - 4.0, height - 4.0).position(2.0, 3.0)
    private val textView = customUiText(if (!parent.isLeftTextAlign && initialText == " ") "" else initialText, this.size)
    //private val textView = container.text(initialText, 16.0, color = Colors.BLACK, font = DefaultTtfFont)
    val controller = TextEditController(textView.textView, uiContainer(textView.size), this, bg = bg,
        hint = textView.uiText(hint.makeStartWithSpace(parent.isLeftTextAlign)) {
            transform {
                this.size = this@UITextInput.size
                textView.size = this.size
                centerYOn(textView).alignX(textView, if (parent.isLeftTextAlign) 0.1 else 0.0, true)
            }
            alpha = 0.5f
        }
    ).apply {
        caretContainer.transform {
            caretContainer.alignY(this@UITextInput, 0.75, false)
        }
    }

    //init { uiScrollable {  } }

    var text: String by controller::text
    var textSize: Float by controller::textSize
    var font: Font by controller::font
    val onReturnPressed: Signal<TextEditController> by controller::onReturnPressed
    val onEscPressed: Signal<TextEditController> by controller::onEscPressed
    val onFocusLost: Signal<TextEditController> by controller::onFocusLost
    var selectionRange: IntRange by controller::selectionRange
    var selectionStart: Int by controller::selectionStart
    var selectionEnd: Int by controller::selectionEnd
    val selectionLength: Int by controller::selectionLength
    fun focus() = controller.focus()
    fun blur() = controller.blur()
    fun selectAll() = controller.selectAll()

    var padding: Margin = Margin(3f, 2f, 2f, 2f)
        set(value) {
            field = value
            onSizeChanged()
        }

    override fun onSizeChanged() {
        bg.size(widthD, heightD)
        container.bounds(Rectangle(0.0, 0.0, widthD, heightD).without(padding))
    }

    init {
        onSizeChanged()
    }

    //override val UIFocusManager.focusView: View get() = this@UITextInput
    //override var tabIndex: Int
    //    get() = TODO("Not yet implemented")
    //    set(value) {}
    //override var focused: Boolean
    //    get() = TODO("Not yet implemented")
    //    set(value) {}
}

var ViewStyles.uiTextInputBackgroundRender: ViewRenderer by ViewStyle(ViewRenderer {
//    ctx2d.rect(Rectangle(0f, 0f, width, height), Colors.WHITE)
})
