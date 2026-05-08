// Test functionality to do with the workouts tab

import com.example.database.*
import com.example.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.*
import kotlin.test.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ViewWorkoutsPageTest {
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

        val client = authenticatedClient()

        val response = client.get("/workouts/view")
        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Push and pull"
        val text2 = "Tough day"
        val text3 = "Simple running on the treadmill"
        val text4 = "A day out at sea"
        val text5 = "Pump!"
        assertTrue(body.contains(text1), "Response body did not contain '$text1'. Body was:\n$body")
        assertTrue(body.contains(text2), "Response body did not contain '$text2'. Body was:\n$body")
        assertTrue(body.contains(text3), "Response body did not contain '$text3'. Body was:\n$body")
        assertTrue(body.contains(text4), "Response body did not contain '$text4'. Body was:\n$body")
        assertTrue(body.contains(text5), "Response body did not contain '$text5'. Body was:\n$body")
    }
}

class ViewWorkoutsPageSearchTests {
    @Test
    fun `get workouts view search route with search=p`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val response = client.get("/workouts/view/search") {
            url {
                parameters.append("search", "p")
            }
        }

        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Push and pull"
        val text2 = "Tough day"
        val text3 = "Simple running on the treadmill"
        val text4 = "A day out at sea"
        val text5 = "Pump!"
        assertTrue(body.contains(text1), "Response body did not contain '$text1'. Body was:\n$body")
        assertFalse(body.contains(text2), "Response body did contain '$text2'. Body was:\n$body")
        assertTrue(body.contains(text3), "Response body did not contain '$text3'. Body was:\n$body")
        assertFalse(body.contains(text4), "Response body did contain '$text4'. Body was:\n$body")
        assertTrue(body.contains(text5), "Response body did not contain '$text5'. Body was:\n$body")
    }

    @Test
    fun `get workouts view search route with search=empty string`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val response = client.get("/workouts/view/search") {
            url {
                parameters.append("search", "")
            }
        }

        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Push and pull"
        val text2 = "Tough day"
        val text3 = "Simple running on the treadmill"
        val text4 = "A day out at sea"
        val text5 = "Pump!"
        assertTrue(body.contains(text1), "Response body did not contain '$text1'. Body was:\n$body")
        assertTrue(body.contains(text2), "Response body did not contain '$text2'. Body was:\n$body")
        assertTrue(body.contains(text3), "Response body did not contain '$text3'. Body was:\n$body")
        assertTrue(body.contains(text4), "Response body did not contain '$text4'. Body was:\n$body")
        assertTrue(body.contains(text5), "Response body did not contain '$text5'. Body was:\n$body")
    }

    @Test
    fun `get workouts view search route with search=abcdefghijklmnop (nonsense value)`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val response = client.get("/workouts/view/search") {
            url {
                parameters.append("search", "abcdefghijklmnop")
            }
        }

        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Push and pull"
        val text2 = "Tough day"
        val text3 = "Simple running on the treadmill"
        val text4 = "A day out at sea"
        val text5 = "Pump!"
        assertFalse(body.contains(text1), "Response body did contain '$text1'. Body was:\n$body")
        assertFalse(body.contains(text2), "Response body did contain '$text2'. Body was:\n$body")
        assertFalse(body.contains(text3), "Response body did contain '$text3'. Body was:\n$body")
        assertFalse(body.contains(text4), "Response body did contain '$text4'. Body was:\n$body")
        assertFalse(body.contains(text5), "Response body did contain '$text5'. Body was:\n$body")
    }
}

class ViewWorkoutsPageFilterTests {
    @Test
    fun `get workouts view search route with filter=Cardio`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val response = client.get("/workouts/view/search") {
            url {
                parameters.append("type", "C")
            }
        }

        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Push and pull"
        val text2 = "Tough day"
        val text3 = "Simple running on the treadmill"
        val text4 = "A day out at sea"
        val text5 = "Pump!"
        assertFalse(body.contains(text1), "Response body did contain '$text1'. Body was:\n$body")
        assertTrue(body.contains(text2), "Response body did not contain '$text2'. Body was:\n$body")
        assertTrue(body.contains(text3), "Response body did not contain '$text3'. Body was:\n$body")
        assertTrue(body.contains(text4), "Response body did not contain '$text4'. Body was:\n$body")
        assertFalse(body.contains(text5), "Response body did contain '$text5'. Body was:\n$body")
    }

    @Test
    fun `get workouts view search route with filter=Cardio+Bodyweight`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val response = client.get("/workouts/view/search") {
            url {
                parameters.append("type", "C")
                parameters.append("type", "B")
            }
        }

        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Push and pull"
        val text2 = "Tough day"
        val text3 = "Simple running on the treadmill"
        val text4 = "A day out at sea"
        val text5 = "Pump!"
        assertTrue(body.contains(text1), "Response body did not contain '$text1'. Body was:\n$body")
        assertTrue(body.contains(text2), "Response body did not contain '$text2'. Body was:\n$body")
        assertTrue(body.contains(text3), "Response body did not contain '$text3'. Body was:\n$body")
        assertTrue(body.contains(text4), "Response body did not contain '$text4'. Body was:\n$body")
        assertFalse(body.contains(text5), "Response body did contain '$text5'. Body was:\n$body")
    }

    @Test
    fun `get workouts view search route with filter=Favourited with no current Favourited workouts`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        client.get("/test/set-all-workouts-as-not-favourited")

        val response = client.get("/workouts/view/search") {
            url {
                parameters.append("favourite", "true")
            }
        }

        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Push and pull"
        val text2 = "Tough day"
        val text3 = "Simple running on the treadmill"
        val text4 = "A day out at sea"
        val text5 = "Pump!"
        assertFalse(body.contains(text1), "Response body did contain '$text1'. Body was:\n$body")
        assertFalse(body.contains(text2), "Response body did contain '$text2'. Body was:\n$body")
        assertFalse(body.contains(text3), "Response body did contain '$text3'. Body was:\n$body")
        assertFalse(body.contains(text4), "Response body did contain '$text4'. Body was:\n$body")
        assertFalse(body.contains(text5), "Response body did contain '$text5'. Body was:\n$body")
    }

    @Test
    fun `get workouts view search route with filter=Favourited with favourited workouts`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        // Set favourite to true
        client.post("/test/change-favourite-workouts") {
            setBody(
                listOf(
                    "workoutName" to "Push and pull"
                ).formUrlEncode()
            )
            headers {
                append(
                    HttpHeaders.ContentType,
                    ContentType.Application.FormUrlEncoded.toString()
                )
            }
        }

        val response = client.get("/workouts/view/search") {
            url {
                parameters.append("favourite", "true")
            }
        }

        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Push and pull"
        val text2 = "Tough day"
        val text3 = "Simple running on the treadmill"
        val text4 = "A day out at sea"
        val text5 = "Pump!"
        assertTrue(body.contains(text1), "Response body did not contain '$text1'. Body was:\n$body")
        assertFalse(body.contains(text2), "Response body did contain '$text2'. Body was:\n$body")
        assertFalse(body.contains(text3), "Response body did contain '$text3'. Body was:\n$body")
        assertFalse(body.contains(text4), "Response body did contain '$text4'. Body was:\n$body")
        assertFalse(body.contains(text5), "Response body did contain '$text5'. Body was:\n$body")
    }
}

class ViewWorkoutPageTest {
    @Test
    fun `get workouts view all workout pages`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val workouts = transaction {Workout.all().toList()}
        if (workouts.isNotEmpty()) {
            for (workout in workouts) {
                val id = workout.id.value.toString()
                val response = client.get("/workouts/{$id}") {
                    url {
                        parameters.append("id", id)
                    }
                }
                println(response.status)
                val body = response.bodyAsText()
                assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
                val name = workout.name
                val desc = workout.desc
                assertTrue(body.contains(name), "Response body did not contain correct name: '$name'. Body was:\n$body")
                assertTrue(body.contains(desc), "Response body did not contain correct description: '$desc'. Body was:\n$body")
            }
        }
        else {
            println("Workouts table is empty")
        }
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

