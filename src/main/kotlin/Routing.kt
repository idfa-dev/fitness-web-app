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
            call.respond(PebbleContent("calendar.peb", mapOf("currentPage" to "calendar")))
        }

        get("/profile") {
            call.respond(PebbleContent("profile.peb", mapOf("currentPage" to "profile")))
        }
    }
}