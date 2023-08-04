package event

import korlibs.event.*
import network.ChatPacket

class PacketEvent(
    val packet: Any,
) : Event(), TEvent<PacketEvent> {
    companion object : EventType<PacketEvent>
    override val type: EventType<PacketEvent> get() = PacketEvent

    override fun toString(): String = "PacketEvent()"
}