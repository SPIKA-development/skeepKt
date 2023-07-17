import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import components.Spawner

object SpawningSystem : IteratingSystem(
    World.family { all(Spawner) }
) {
    override fun onTickEntity(entity: Entity) {
        
    }
}