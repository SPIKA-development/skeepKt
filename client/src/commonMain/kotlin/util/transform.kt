package util

import event.ResizedEvent
import korlibs.korge.view.View

fun <T : View> T.transform(code: T.() -> Unit): T {
    code(this)
    onEvent(ResizedEvent) { code(this) }
    return this
}