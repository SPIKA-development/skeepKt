import korlibs.image.format.*
import korlibs.io.async.async
import korlibs.io.file.std.resourcesVfs
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope

object SpriteAssets {
    @DelicateCoroutinesApi
    private var images: Map<String, ImageDataContainer> = GlobalScope.async {
            mapOf("test" to resourcesVfs["test.ase"])
                .mapValues { it.value.readImageDataContainer(ASE.toProps()) }
        }.awaitGet()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> Deferred<T>.awaitGet(): T {
        @Suppress("ControlFlowWithEmptyBody")
        while (!isCompleted) {}
        return getCompleted()
    }

    fun getImage(name: String, slice: String = "") : ImageData {
        return if (images[name] != null) {
            if (slice.isEmpty()) {
                images[name]!!.default
            } else {
                if (images[name]!![slice] != null) {
                    images[name]!![slice]!!
                } else {
                    throw RuntimeException("Slice '$slice' of image '$name' not found in asset images!")
                }
            }
        } else {
            throw RuntimeException("Image '$name' not found in asset images!")
        }
    }
}

