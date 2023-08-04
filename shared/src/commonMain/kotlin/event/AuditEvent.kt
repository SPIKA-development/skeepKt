package event

import korlibs.event.*
import network.ChatPacket

class ChatEvent(
    val chatPacket: ChatPacket,
) : Event(), TEvent<ChatEvent> {
    companion object : EventType<ChatEvent>
    override val type: EventType<ChatEvent> get() = ChatEvent

    override fun toString(): String = "ChatEvent()"
}