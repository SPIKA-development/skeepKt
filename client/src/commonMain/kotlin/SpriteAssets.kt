import korlibs.image.format.*
import korlibs.io.file.std.resourcesVfs

object SpriteAssets {

    private var images = mapOf<String, ImageDataContainer>()

    suspend fun load() {
        images = mapOf("test" to resourcesVfs["test.ase"])
            .mapValues { it.value.readImageDataContainer(ASE.toProps()) }
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

