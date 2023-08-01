package application

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import kotlin.reflect.KProperty

fun genKey() = UUID.generateUUID().toString().replace("-", "").substring(0, 14)
object EnvVar {
    val serverPort by IntEnv { 8080 }
    val sslPort by IntEnv { 8443 }
    val sslPassword by StringEnv { "cUF6ka6DhmEedX" }
    val domains by ListEnv { listOf("localhost") }
}

abstract class Env<T>(private val default: () -> T) {
    abstract fun convert(string: String): T
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        runCatching { System.getenv(property.name).run(::convert) }
            .getOrNull()?: default()
}

class StringEnv(default: () -> String) : Env<String>(default) {
    override fun convert(string: String) = string
}

class ListEnv(default: () -> List<String>) : Env<List<String>>(default) {
    override fun convert(string: String) = string.split(",")
}

class IntEnv(default: () -> Int) : Env<Int>(default) {
    override fun convert(string: String) = string.toInt()
}