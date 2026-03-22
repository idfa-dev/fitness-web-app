// Request handlers for exercise details
// Handles responses and templates

package com.example

import com.example.database.Exercise
import io.ktor.server.application.ApplicationCall
import io.ktor.server.pebble.respondTemplate
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

suspend fun ApplicationCall.exercises() {
    suspendTransaction {
        val exercises = Exercise.all().sortedBy {it.name}.toList()
        respondTemplate("exercises.peb", mapOf("exercises" to exercises))
    }
}