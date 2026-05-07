// Test functionality to do with Workouts.kt

import com.example.database.*
import com.example.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.client.*
import io.ktor.client.plugins.cookies.HttpCookies
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class WorkoutsTest {
    @Test
    fun `get workouts route`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "test" to "true"
            )
        }
        application {
            module()
        }
        val response = client.get("/workouts")
        println(response.status)
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(),"View Workouts")
        assertContains(response.bodyAsText(),"Past Workouts")
    }

    @Test
    fun `get workouts view route`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = createClient {
            install(HttpCookies) {
            }
        }

        // Set user session
        client.post("/_test/sign-in") {
            setBody(
                listOf(
                    "userID" to "1",
                    "username" to "regulardude123"
                ).formUrlEncode()
            )
            headers {
                append(
                    HttpHeaders.ContentType,
                    ContentType.Application.FormUrlEncoded.toString()
                )
            }
        }
        val response = client.get("/workouts/view")
        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        assertTrue(body.contains("Favourited"), "Response body did not contain 'Favourited'. Body was:\n$body")
        assertTrue(body.contains("checkbox"), "Response body did not contain 'checkbox'. Body was:\n$body")
    }
}