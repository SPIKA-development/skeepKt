package application.configuration

import io.github.cdimascio.dotenv.dotenv
import kotlin.reflect.KProperty

private val dotenv = dotenv()

object EnvVar {
    val serverPort by IntEnv { 8080 }
    val sslPort by IntEnv { 8443 }
    val sslPassword by StringEnv { "angang" }
    val ADMIN_KEY by StringEnv()
}

abstract class Env<T>(private val default: (() -> T)?) {
    abstract fun convert(string: String): T
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Any? {
        val key = property.name
        return runCatching { System.getenv(key).run(::convert) }
            .getOrNull()?: dotenv.get(key) ?: default?.invoke()?: throw AssertionError("$key is not exists")
    }
}

class StringEnv(default: (() -> String)? = null) : Env<String>(default) {
    override fun convert(string: String) = string
}

class ListEnv(default: () -> List<String>) : Env<List<String>>(default) {
    override fun convert(string: String) = string.split(",")
}

class IntEnv(default: () -> Int) : Env<Int>(default) {
    override fun convert(string: String) = string.toInt()
}