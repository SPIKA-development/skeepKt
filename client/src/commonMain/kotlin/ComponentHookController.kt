import com.github.quillraven.fleks.ComponentHook

interface ComponentHookController<T> {
    val onAdded: ComponentHook<T>
    val onRemoved: ComponentHook<T>
}