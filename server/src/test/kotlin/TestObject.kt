import kotlinx.serialization.Serializable

@Serializable
data class MS(val ms: Long = System.currentTimeMillis())