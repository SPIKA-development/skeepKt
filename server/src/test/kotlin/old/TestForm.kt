package old

import network.LoginRequest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import korlibs.io.async.suspendTest
import kotlin.test.Test

class TestForm {

//    @BeforeTest
//    fun startUpApplication() = main()

    @Test
    fun test() {
        suspendTest {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json()
                }
                install(HttpCookies) {
                    storage = AcceptAllCookiesStorage()
                }
            }
            val currentUrl = "http://127.0.0.1:8080"
            println("-".repeat(100))
            println(client.get("$currentUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest("jetbrinaer"))
            }.status)
            println(client.cookies(currentUrl))
            // STOPSHIP: client is not working just like a web browser...
            println("-".repeat(100))
        }
    }
}