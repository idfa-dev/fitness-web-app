package com.example

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600 // 60 minutes
        }
        cookie<UserSession>("workout_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600 // 60 minutes
        }
    }
}

@Serializable
data class UserSession(val id: String, val username: String)
data class WorkoutSession(val workoutSessionId: String, val userId: String)