import application.main
import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import network.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class ClientTest {
    @BeforeTest
    fun startupServer() = runBlocking { application.startTestServer() }

    @BeforeTest
    fun startHttpClient() = runBlocking { main() }

    @Test
    fun testCreateRoom() = runBlocking {
        login()
        val viewedRooms = createRoom()
        println(viewedRooms)
    }

    @Test
    fun testListRoom() = runBlocking {
        login()
        val viewedRooms = getViewedRooms()
        println(viewedRooms)
    }

}
