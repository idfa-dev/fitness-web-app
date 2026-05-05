package com.example

//DB
import com.example.database.*

//Server
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.pebble.PebbleContent
import io.ktor.server.request.receiveParameters
import io.ktor.server.sessions.*

//jav
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

//Authentication imports
import com.example.database.authenticateUser //importing authentication from auth.kt
import com.example.database.authenticatePT
import com.example.database.doesCollide //importing collision checker from auth.kt
import com.example.database.User
import com.example.database.PT
import com.example.database.signInHandler

//SQL ( might be unnecessary )
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction //to mod db


fun Application.configureRouting() {
    routing {

        staticResources("/static", "static")

        get("/") {
            call.respond(PebbleContent("landing.peb", mapOf("currentPage" to "landing")))
        }

        get("/home") {
            val user = call.sessions.get<UserSession>()
            print(user?.id)
            print(user?.username)
            if (user != null) {
                call.respond(PebbleContent("home.peb", mapOf("currentPage" to "home")))
            }
            else {
                call.respond(PebbleContent("landing.peb", mapOf("currentPage" to "landing")))
            }
        }

        get("/workouts") {
            call.respond(PebbleContent("workouts.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                call.displayWorkout(id)
            }
        }

        get("/workouts/view") {call.displayWorkouts()}

        get("/workouts/view/search") {call.searchWorkouts()}

        get("/workouts/create") {
            call.respond(PebbleContent("create_workout.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/create/cardio") {
            call.respond(PebbleContent("create_cardio_workout.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/create/bodyweight") {
            call.respond(PebbleContent("create_bodyweight_workout.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/create/resistance") {
            call.respond(PebbleContent("create_resistance_workout.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/create/mixed") {
            call.respond(PebbleContent("create_mixed_workout.peb", mapOf("currentPage" to "workouts")))
        }

        get("/exercises") {call.exercises()}

        get("/exercises/search") {call.searchExercises()}

        get("/exercises/{id}") {call.exercise()}

        get("/calendar") {
            val now = LocalDate.now()
            val month = now.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()
            val year = now.year
            val firstDay = now.withDayOfMonth(1)
            val lastDay = now.withDayOfMonth(now.lengthOfMonth())
            val startWeekday = (firstDay.dayOfWeek.value + 6) % 7 // Monday first

            val days = mutableListOf<String>()
            repeat(startWeekday) { days.add("") }
            for (i in 1..lastDay.dayOfMonth) days.add(i.toString())

            val remainder = days.size % 7
            if (remainder != 0) repeat(7 - remainder) { days.add("") }

            val weeks = days.chunked(7)

            val calendarTable = buildString {
                append("<div class='month-year'>$month $year</div>")
                append("<table class='calendar'>")
                append("<tr>")
                listOf("MON","TUE","WED","THU","FRI","SAT","SUN").forEach { append("<th>$it</th>") }
                append("</tr>")
                weeks.forEach { week ->
                    append("<tr>")
                    week.forEach { day ->
                        if(day.isEmpty()) append("<td></td>")
                        else append("<td><span class='day-number'>$day</span></td>")
                    }
                    append("</tr>")
                }
                append("</table>")
            }

            call.respond(PebbleContent("calendar.peb", mapOf(
                "currentPage" to "calendar",
                "calendarTable" to calendarTable
            )))
        }


        get("/profile") {
            call.respond(PebbleContent("profile.peb", mapOf("currentPage" to "profile")))
        }

        get("/profile/profile_info") {
            val user = transaction {
                User.find { Users.username eq "regulardude123" }.firstOrNull()
            }

            val context = mutableMapOf<String, Any>(
                "currentPage" to "profile"
            )

            user?.let { context["user"] = it }
            call.respond(PebbleContent("profile_info.peb", context))
        }

        post("/profile/profile_info") {
            val params = call.receiveParameters()

            val fname = params["fname"]
            val sex = params["sex"]
            val age = params["age"]
            val height = params["height"]?.toFloatOrNull()
            val weight = params["weight"]?.toFloatOrNull()

            println("POST ROUTE HIT")

            transaction {
                val user = User.find { Users.username eq "regulardude123" }.firstOrNull()

                if (user != null) {
                    if (!fname.isNullOrBlank()) user.fname = fname
                    if (!sex.isNullOrBlank()) user.sex = sex
                    height?.let { user.height = it }
                    weight?.let { user.weight = it }
                    if (!age.isNullOrBlank()) {
                        user.age = age.toInt()
                    }
                }
            }
            call.respondRedirect("/profile/profile_info")
        }

        get("/sign-in") {
            call.respond(PebbleContent("sign-in.peb", mapOf("currentPage" to "sign-in")))
        }

        post("/sign-in") { //This whole section will be added to Auth.kt eventually or like modularized
            
            signInHandler(call)
            
        }

        get("/sign-up") {
            call.respond(PebbleContent("sign-up.peb", mapOf("currentPage" to "sign-up")))
        }

        post("/sign-up") {  //This whole section will be added to User/Users eventually.
            signUpHandler(call)
        }
    }
}