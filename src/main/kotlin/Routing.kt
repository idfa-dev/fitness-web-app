package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.pebble.PebbleContent


fun Application.configureRouting() {
    routing {

        staticResources("/static", "static")

        get("/") {
            call.respond(PebbleContent("home.peb", mapOf("currentPage" to "home")))
        }

        get("/workouts") {
            call.respond(PebbleContent("workouts.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/cardio") {
            call.respond(PebbleContent("cardio_workouts.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/bodyweight") {
            call.respond(PebbleContent("bodyweight_workouts.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/resistance") {
            call.respond(PebbleContent("resistance_workouts.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/mixed") {
            call.respond(PebbleContent("mixed_workouts.peb", mapOf("currentPage" to "workouts")))
        }

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

        get("/exercises") {
            call.respond(PebbleContent("exercises.peb", mapOf("currentPage" to "exercises")))
        }

        get("/calendar") {
            val now = java.time.LocalDate.now()
            val month = now.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH).uppercase()
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
    }
}