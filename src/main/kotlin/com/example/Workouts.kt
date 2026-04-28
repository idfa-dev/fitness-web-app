// Request handlers for workout details
// Handles responses and templates

package com.example

import com.example.database.WorkoutExercise
import com.example.database.Workout
import com.example.database.Exercise
import com.example.database.User
import com.example.database.SavedWorkout
import com.example.database.getUserIdByUsername
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.text.toInt

suspend fun ApplicationCall.cardioWorkouts() {
    suspendTransaction {
        val workouts = Workout.all().filter {it.type==0}.toList()
        respondTemplate("display_workouts.peb", mapOf("workouts" to workouts))
    }
}

suspend fun ApplicationCall.bodyweightWorkouts() {
    suspendTransaction {
        val workouts = Workout.all().filter {it.type==1}.toList()
        respondTemplate("display_workouts.peb", mapOf("workouts" to workouts))
    }
}

suspend fun ApplicationCall.resistanceWorkouts() {
    suspendTransaction {
        val workouts = Workout.all().filter {it.type==2}.toList()
        respondTemplate("display_workouts.peb", mapOf("workouts" to workouts))
    }
}

suspend fun ApplicationCall.mixedWorkouts() {
    suspendTransaction {
        val workouts = Workout.all().filter {it.type==3}.toList()
        respondTemplate("display_workouts.peb", mapOf("workouts" to workouts))
    }
}

suspend fun ApplicationCall.savedWorkouts() {
    suspendTransaction {
        val userSession = sessions.get<UserSession>()

        if (userSession != null) {
            val user = User.all().single {it.id.toString() == userSession.id}
            // Find all workouts in SavedWorkouts with user = user
            val savedWorkouts = SavedWorkout.all().filter {it.user == user}.toList()
            respondTemplate("display_saved_workouts.peb", mapOf("savedWorkouts" to savedWorkouts))
        }
    }
}

// Class definition for workout object to be passed in the respondTemplate call for display_workout (below)
class WorkoutObject (
    var workout: Workout,
    var exercises: MutableList<WorkoutExercise>
)

suspend fun ApplicationCall.displayWorkout(id: Int) {
    suspendTransaction {
        val workout = Workout.get(id)
        val exercises = WorkoutExercise.all().filter {it.workout==workout}.sortedBy {it.order}.toMutableList() // Sorting by order

        val wo = WorkoutObject(
            workout = workout, // Containing the workout content (name, desc, type)
            exercises = exercises // Containing the workout exercise content (workout, exercise, order, sets, reps, duration)
        )

        respondTemplate("display_workout.peb", mapOf("workout" to wo))
    }
}

suspend fun ApplicationCall.saveWorkout() {
    suspendTransaction {
        val parameters = receiveParameters()
        val workoutID = parameters["workoutID"] ?: "Input not received."

        val userSession = sessions.get<UserSession>()
        if (userSession != null) {
            val user = User.all().first {it.id.toString() == userSession.id}
            val workout = Workout.all().first {it.id.toString() == workoutID}
            SavedWorkout.new {
                this.user = user
                this.workout = workout
            }
        }

        respondTemplate("workouts.peb", mapOf("workout" to "workout"))
    }
}