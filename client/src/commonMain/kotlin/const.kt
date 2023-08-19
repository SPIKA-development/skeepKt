import io.ktor.client.engine.*

typealias KtorEngine = HttpClientEngineFactory<HttpClientEngineConfig>

lateinit var currentUrl: String
lateinit var version: String
lateinit var engine: KtorEngine
