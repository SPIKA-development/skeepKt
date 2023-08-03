package event

import korlibs.event.*
import korlibs.math.geom.*
import korlibs.time.*
import network.Chat

class ChatEvent(
    val chat: Chat,
) : Event(), TEvent<ChatEvent> {
    companion object : EventType<ChatEvent>
    override val type: EventType<ChatEvent> get() = ChatEvent

    override fun toString(): String = "ChatEvent()"
}