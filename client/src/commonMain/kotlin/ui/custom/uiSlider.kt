package ui.custom

import korlibs.image.color.Colors
import korlibs.image.text.TextAlignment
import korlibs.io.async.Signal
import korlibs.io.util.toStringDecimal
import korlibs.korge.input.onMouseDrag
import korlibs.korge.ui.UIView
import korlibs.korge.ui.uiContainer
import korlibs.korge.ui.uiMaterialLayer
import korlibs.korge.view.*
import korlibs.korge.view.property.ViewProperty
import korlibs.math.geom.RectCorners
import korlibs.math.geom.Size
import korlibs.memory.clamp
import korlibs.memory.convertRange
import korlibs.memory.nearestAlignedTo
import util.ColorPalette

inline fun Container.customUiSlider(
    value: Number = CustomUISlider.DEFAULT_VALUE,
    min: Number = CustomUISlider.DEFAULT_MIN,
    max: Number = CustomUISlider.DEFAULT_MAX,
    step: Number = CustomUISlider.DEFAULT_STEP,
    decimalPlaces: Int = CustomUISlider.decimalPlacesFromStep(step.toDouble()),
    size: Size = CustomUISlider.DEFAULT_SIZE,
    block: @ViewDslMarker CustomUISlider.() -> Unit = {}
): CustomUISlider = CustomUISlider(value, min, max, step, decimalPlaces, size).addTo(this).apply(block)

class CustomUISlider(
    value: Number = DEFAULT_VALUE, min: Number = DEFAULT_MIN, max: Number = DEFAULT_MAX, step: Number = DEFAULT_STEP,
    decimalPlaces: Int = DEFAULT_DECIMAL_PLACES,
    size: Size = CustomUISlider.DEFAULT_SIZE,
    //width: Float = DEFAULT_WIDTH, height: Float = DEFAULT_HEIGHT
) : UIView(size) {
    companion object {
        const val DEFAULT_VALUE = 0
        const val DEFAULT_MIN = 0
        const val DEFAULT_MAX = 100
        const val DEFAULT_STEP = 1f
        const val DEFAULT_DECIMAL_PLACES = 1
        val DEFAULT_SIZE = Size(128, 16)
        const val NO_STEP = 0f

        fun decimalPlacesFromStep(step: Double): Int = when {
            step >= 1.0 -> 0
            step > 0.01 -> 1
            else -> 2
        }
    }

    val bg = uiContainer(size)
    val button = uiMaterialLayer(size) {
        shadowColor = Colors.TRANSPARENT
        bgColor = Colors.TRANSPARENT
        borderColor = ColorPalette.hover
        borderSize = size.height / 10
        radius = RectCorners(borderSize*2)
    }
//    val text = text("", alignment = TextAlignment.MIDDLE_CENTER, color = ColorPalette.text)

    val onChange: Signal<Float> = Signal()

    @ViewProperty
    var min: Double = min.toDouble()
        set(value) {
            if (field != value) {
                field = value
                reposition()
            }
        }

    @ViewProperty
    var max: Double = max.toDouble()
        set(value) {
            if (field != value) {
                field = value
                reposition()
            }
        }

    @ViewProperty
    var step: Double = step.toDouble()
        set(value) {
            if (field != value) {
                field = value
                reposition()
            }
        }

    @ViewProperty
    var value: Double = value.toDouble()
        set(value) {
            val rvalue = value.clamp(min, max).nearestAlignedTo(step)
            if (rvalue != field) {
                field = rvalue
                reposition()
                valueChanged()
                onChange(rvalue.toFloat())
            }
        }

    @ViewProperty
    var decimalPlaces: Int = decimalPlaces
        set(value) {
            field = value
            valueChanged()
        }

    private val maxXPos: Double get() = (bg.widthD - button.widthD)

    //val clampedValue: Int get() = value.clamp(min, max)

    private fun reposition() {
        this@CustomUISlider.button.xD = value.convertRange(min, max, 0.0, maxXPos).toDouble()
    }

    override fun onSizeChanged() {
        bg.size(widthD, heightD)
        button.size(heightD, heightD)
//        text.xy(widthD - 16.0, 0.0)
        reposition()
    }

    private fun valueChanged() {
        //text.text = value.toStringDecimal(decimalPlaces = decimalPlaces, skipTrailingZeros = true)
//        text.text = value.toStringDecimal(decimalPlaces = decimalPlaces, skipTrailingZeros = false)
    }

    init {
        onSizeChanged()
        valueChanged()
    }

    init {
        this.onMouseDrag {
            this@CustomUISlider.value = (localMousePos(views).x - button.widthD / 2).convertRange(0.0, maxXPos, this@CustomUISlider.min, this@CustomUISlider.max)
        }
    }
}

fun <T : CustomUISlider> T.changed(block: (Float) -> Unit): T {
    onChange.add(block)
    return this
}
