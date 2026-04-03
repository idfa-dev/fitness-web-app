// Request handlers for workout details
// Handles responses and templates

package com.example

import com.example.database.WorkoutExercise
import com.example.database.Workout
import com.example.database.Exercise
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.response.respond
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import kotlin.text.toInt

suspend fun ApplicationCall.cardio_workouts() {
    suspendTransaction {
        val workouts = Workout.all().filter {it.type==0}.toList()
        respondTemplate("display_workouts.peb", mapOf("workouts" to workouts))
    }
}

suspend fun ApplicationCall.bodyweight_workouts() {
    suspendTransaction {
        val workouts = Workout.all().filter {it.type==1}.toList()
        respondTemplate("display_workouts.peb", mapOf("workouts" to workouts))
    }
}

suspend fun ApplicationCall.resistance_workouts() {
    suspendTransaction {
        val workouts = Workout.all().filter {it.type==2}.toList()
        respondTemplate("display_workouts.peb", mapOf("workouts" to workouts))
    }
}

suspend fun ApplicationCall.mixed_workouts() {
    suspendTransaction {
        val workouts = Workout.all().filter {it.type==3}.toList()
        respondTemplate("display_workouts.peb", mapOf("workouts" to workouts))
    }
}

suspend fun ApplicationCall.display_workout(workout: Workout) {
    suspendTransaction {
        val exercises = WorkoutExercise.all().filter {it.workout==Workout}.toList()
        respondTemplate("display_workout.peb", mapOf("exercises" to exercises))
    }
}