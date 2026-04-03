package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.pebble.PebbleContent
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

import com.example.database.authenticateUser //importing authentication from auth.kt
import io.ktor.server.request.receiveParameters

fun Application.configureRouting() {
    routing {

        staticResources("/static", "static")

        get("/") {
            call.respond(PebbleContent("home.peb", mapOf("currentPage" to "home")))
        }

        get("/workouts") {
            call.respond(PebbleContent("workouts.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/cardio") {call.cardio_workouts()}

        get("/workouts/bodyweight") {call.bodyweight_workouts()}

        get("/workouts/resistance") {call.resistance_workouts()}

        get("/workouts/mixed") {call.mixed_workouts()}

        get("/workouts/saved") {
            call.respond(PebbleContent("saved_workouts.peb", mapOf("currentPage" to "workouts")))
        }

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
            call.respond(PebbleContent("profile_info.peb", mapOf("currentPage" to "profile")))
        }

        get("/sign-in") {
            call.respond(PebbleContent("sign-in.peb", mapOf("currentPage" to "sign-in")))
        }

        post("/sign-in") {  // Handling sign-in, for now only works for regulardude123, password regular

            
            val parameters = call.receiveParameters()

            val username = parameters["username"] ?: "Input not received."  //second case for input check
            val password = parameters["password"] ?: "Input not received."

            println("Username: $username, Password: $password") //input check for sign-in
            
            val isValid = authenticateUser(username, password)

            // 
            if (isValid) {
                call.respondRedirect("/")   //redirects ot the actual home page
            }
            else 
            {
                call.respondText("Invalid details. Please try again.")  //error message on failure
            }

        }

        get("/sign-up") {
            call.respond(PebbleContent("sign-up.peb", mapOf("currentPage" to "sign-up")))
        }

        get("/landing") {
            call.respond(PebbleContent("landing.peb", mapOf("currentPage" to "landing")))
        }
    }
}