package com.example

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 10800 // 3 Hours
        }
        cookie<CurrentWorkoutSession>("current_workout_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 10800 // 3 Hours
        }
    }
}

@Serializable
data class UserSession(val id: String, val username: String)

@Serializable
data class CurrentWorkoutSession(val workoutSessionID: String, val userID: String)