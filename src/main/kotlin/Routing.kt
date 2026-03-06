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