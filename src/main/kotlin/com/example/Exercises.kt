// Request handlers for exercise details
// Handles responses and templates

package com.example

import com.example.database.Exercise
import com.example.database.Exercises
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.response.respond
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

suspend fun ApplicationCall.exercises() {
    suspendTransaction {
        val exercises = Exercise.all().sortedBy {it.name}.toList()
        respondTemplate("exercises.peb", mapOf("exercises" to exercises, "currentPage" to "exercises"))
    }
}

suspend fun ApplicationCall.exercise() {
    suspendTransaction {
        val result = runCatching {
            parameters["id"]?.let {
                Exercise.findById(it.toInt())
            }
        }

        when (val exercise = result.getOrNull()) {
            null -> respond(HttpStatusCode.NotFound)
            else -> {
                respondTemplate("view_exercise.peb", mapOf("exercise" to exercise))
            }
        }
    }
}

suspend fun ApplicationCall.searchExercises() {
    suspendTransaction {
        val search = parameters["search"]

        if (search == null) {
            respond(HttpStatusCode.BadRequest)
        }
        else {
            val exercises = Exercise.all().filter {it.name.contains(search, true)}.sortedBy {it.name}.toList()
            respondTemplate("exercises.peb", mapOf("exercises" to exercises))
        }
    }
}