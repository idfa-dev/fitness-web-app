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
import org.jetbrains.exposed.v1.core.dao.id.EntityID
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

// Class definition for workout object to be passed in the respondTemplate call for display_workout (below)
class WorkoutObject (
    var workout: Workout,
    var exercises: MutableList<WorkoutExercise>,
    var exerciseInfo: MutableList<Exercise>
)

suspend fun ApplicationCall.display_workout(id: Int) {
    suspendTransaction {
        val workout = Workout.get(id)
        val exercises = WorkoutExercise.all().filter {it.workout==workout}.toMutableList()
        val exerciseInfo = mutableListOf<Exercise>()
        for (x in 0..exercises.size - 1) {
            var exercise = Exercise.all().firstOrNull {it.id==exercises[x].id}
            if (exercise != null) {
                exerciseInfo.add(exercise)
            }
        }
        val wo = WorkoutObject(
            workout = workout,
            exercises = exercises,
            exerciseInfo = exerciseInfo
        )

        respondTemplate("display_workout.peb", mapOf("workout" to wo))
    }
}