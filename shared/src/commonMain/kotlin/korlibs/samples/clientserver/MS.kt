package korlibs.samples.clientserver

import korlibs.time.DateTime
import kotlinx.serialization.Serializable

@Serializable
data class MS(
    val ms: Long = DateTime.now().unixMillisLong
)

@Serializable
data class TeleportPacket(
    val to: Pos
)

@Serializable
data class Pos(val x: Int, val y: Int)
