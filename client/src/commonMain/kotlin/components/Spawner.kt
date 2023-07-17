package components

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class Spawner(

): Component<Spawner> {
    override fun type() = Spawner
    companion object : ComponentType<Spawner>()
}