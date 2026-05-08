// Test functionality to do with the exercises tab

import com.example.database.*
import com.example.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.*
import kotlin.test.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExercisesPageTest {
    @Test
    fun `get exercises route`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val exercises =  transaction { Exercise.all().toList() }
        val exerciseCount = exercises.size
        if (exercises.isNotEmpty()) {
            val response = client.get("/exercises")
            println(response.status)
            val body = response.bodyAsText()
            assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
            val linkCount = Regex("<a href=\"/exercises").findAll(body).count() - 1
            assertEquals(linkCount, exerciseCount, "Response body contained $linkCount exercises, but $exerciseCount exercises exist in database:\n$body")
        }
    }
}

class ViewExercisePageTest {
    @Test
    fun `get exercises view all exercise pages`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val exercises = transaction {Exercise.all().toList()}
        if (exercises.isNotEmpty()) {
            for (exercise in exercises) {
                val id = exercise.id.value.toString()
                val response = client.get("/exercises/{$id}") {
                    url {
                        parameters.append("id", id)
                    }
                }
                println(response.status)
                val body = response.bodyAsText()
                assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
                val url = exercise.url
                // Reason for no assertTrue test for exercise.name (apostrophes are escaped in html)
                assertTrue(body.contains(url), "Response body did not contain correct YouTube video url: '$url'. Body was:\n$body")
            }
        }
        else {
            println("Exercises table is empty")
        }
    }
}

class ViewExercisesSearchPageTests {
    @Test
    fun `get exercises search route with search=c`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val response = client.get("/exercises/search") {
            url {
                parameters.append("search", "c")
            }
        }
        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Barbell bench press"
        val text2 = "Clean and jerk"
        val text3 = "Rowing machine"
        assertTrue(body.contains(text1), "Response body did not contain '$text1'. Body was:\n$body")
        assertTrue(body.contains(text2), "Response body did not contain '$text2'. Body was:\n$body")
        assertTrue(body.contains(text3), "Response body did not contain '$text3'. Body was:\n$body")
    }

    @Test
    fun `get exercises search route with search=empty string`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val response = client.get("/exercises/search") {
            url {
                parameters.append("search", "")
            }
        }
        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Barbell bench press"
        val text2 = "Barbell squat"
        val text3 = "Clean and jerk"
        val text4 = "Farmer" // To prevent html escape letter from messing with the assert (')
        val text5 = "Pull ups"
        val text6 = "Push ups"
        val text7 = "Rowing machine"
        val text8 = "Treadmill"
        assertTrue(body.contains(text1), "Response body did not contain '$text1'. Body was:\n$body")
        assertTrue(body.contains(text2), "Response body did not contain '$text2'. Body was:\n$body")
        assertTrue(body.contains(text3), "Response body did not contain '$text3'. Body was:\n$body")
        assertTrue(body.contains(text4), "Response body did not contain '$text4'. Body was:\n$body")
        assertTrue(body.contains(text5), "Response body did not contain '$text5'. Body was:\n$body")
        assertTrue(body.contains(text6), "Response body did not contain '$text6'. Body was:\n$body")
        assertTrue(body.contains(text7), "Response body did not contain '$text7'. Body was:\n$body")
        assertTrue(body.contains(text8), "Response body did not contain '$text8'. Body was:\n$body")
    }

    @Test
    fun `get exercises search route with search=abcdefghijklmnop (nonsense value)`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "app.test" to "true"
            )
        }

        application {
            module()
        }

        val client = authenticatedClient()

        val response = client.get("/exercises/search") {
            url {
                parameters.append("search", "abcdefghijklmnop")
            }
        }
        println(response.status)
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status, "Expected 200 but got ${response.status}")
        val text1 = "Barbell bench press"
        val text2 = "Barbell squat"
        val text3 = "Clean and jerk"
        val text4 = "Farmer" // To prevent html escape letter from messing with the assert (')
        val text5 = "Pull ups"
        val text6 = "Push ups"
        val text7 = "Rowing machine"
        val text8 = "Treadmill"
        assertFalse(body.contains(text1), "Response body did contain '$text1'. Body was:\n$body")
        assertFalse(body.contains(text2), "Response body did contain '$text2'. Body was:\n$body")
        assertFalse(body.contains(text3), "Response body did contain '$text3'. Body was:\n$body")
        assertFalse(body.contains(text4), "Response body did contain '$text4'. Body was:\n$body")
        assertFalse(body.contains(text5), "Response body did contain '$text5'. Body was:\n$body")
        assertFalse(body.contains(text6), "Response body did contain '$text6'. Body was:\n$body")
        assertFalse(body.contains(text7), "Response body did contain '$text7'. Body was:\n$body")
        assertFalse(body.contains(text8), "Response body did contain '$text8'. Body was:\n$body")
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