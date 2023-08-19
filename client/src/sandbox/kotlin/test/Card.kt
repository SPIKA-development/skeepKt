package test

import korlibs.image.format.readBitmap
import korlibs.io.async.launchImmediately
import korlibs.io.async.runBlockingNoSuspensions
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.Korge
import korlibs.korge.view.image
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import util.launchNow
import kotlin.test.Test

class Card {
    @Test
    fun testCard() {
        launchImmediately(GlobalScope.coroutineContext) {
            Korge {
                image(resourcesVfs["images/cards/d_card.png"].readBitmap())
            }
        }
    }

}
