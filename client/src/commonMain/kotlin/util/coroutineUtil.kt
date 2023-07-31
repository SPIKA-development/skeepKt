package util

import korlibs.io.async.launchImmediately
import kotlinx.coroutines.CoroutineScope
import org.koin.mp.KoinPlatform.getKoin
import kotlin.coroutines.CoroutineContext

fun launchNow(context: CoroutineContext = getKoin().get<CoroutineContext>(), callback: suspend () -> Unit) = CoroutineScope(context).launchImmediately(callback)
