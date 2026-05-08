// Test functionality to do with the current-workout tab

import com.example.database.*
import com.example.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.*
import kotlin.test.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CurrentWorkoutPageTests {
    @Test
    fun `get current workout route (no current WorkoutSession)`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val response = client.get("/current-workout")
        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Use a template"
        val text2 = "Start from scratch"
        assertTrue(body.contains(text1), "Response body did not contain '$text1'. Body was:\n$body")
        assertTrue(body.contains(text2), "Response body did not contain '$text2'. Body was:\n$body")
    }

    @Test
    fun `get current workout route (current WorkoutSession is set)`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        // Set CurrentWorkoutSession
        client.post("/test/create-workout-session") {
            setBody (
                listOf(
                    "userID" to "1",
                    "username" to "regulardude123"
                ).formUrlEncode()
            )
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }
        }

        val response = client.get("/current-workout")
        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Add Exercise"
        val text2 = "End Workout"
        assertTrue(body.contains(text1), "Response body did not contain '$text1'. Body was:\n$body")
        assertTrue(body.contains(text2), "Response body did not contain '$text2'. Body was:\n$body")
    }
}

/*
TEST LAYOUT
@Test
    fun `test name`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val response = client.get("/route")
        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "ex text"
        val text2 = "ex text 2"
        assertTrue(body.contains(text1), "Response body did not contain '$text1'. Body was:\n$body")
        assertTrue(body.contains(text2), "Response body did not contain '$text2'. Body was:\n$body")
    }
*/