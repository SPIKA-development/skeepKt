package event

import korlibs.event.Event
import korlibs.event.EventType
import korlibs.event.TEvent

class PacketEvent(
    val packet: Any,
) : Event(), TEvent<PacketEvent> {
    companion object : EventType<PacketEvent>
    override val type: EventType<PacketEvent> get() = PacketEvent

    override fun toString(): String = "PacketEvent()"
}