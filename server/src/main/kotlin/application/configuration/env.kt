package application.configuration

import io.github.cdimascio.dotenv.dotenv
import kotlin.reflect.KProperty

private val dotenv = dotenv {
    ignoreIfMissing = true
}

object EnvVar {
    val serverPort by IntEnv { 8080 }
    val sslPort by IntEnv { 8443 }
    val sslPassword by StringEnv { "angang" }
    val ADMIN_KEY by StringEnv()
    val DB_URL by StringEnv()
    val DB_USERNAME by StringEnv()
    val DB_PASSWORD by StringEnv()
}

abstract class Env<T>(private val default: (() -> T)?) {
    abstract fun convert(string: String): T
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T & Any {
        val key = property.name
        return runCatching { System.getenv(key).run(::convert) }
            .getOrNull()?: dotenv.get(key)?.run(::convert)
        ?: default!!.invoke()!!
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