// Request handlers for workout details
// Handles responses and templates

package com.example

import com.example.database.WorkoutSession
import com.example.database.WorkoutSessionExercise
import com.example.database.WorkoutSessionSet
import com.example.database.WorkoutExercise
import com.example.database.Workout
import com.example.database.Exercise
import com.example.database.User
import com.example.database.getListOfWorkoutSessionExercises
import com.example.database.getUserIdByUsername
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.pebble.PebbleContent
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.text.toInt

// Class definition for workout object to be passed in the respondTemplate call for display_workout (below)
data class WorkoutObject (
    var workout: Workout,
    var exercises: List<WorkoutExercise>,
    var types: Set<String>
)

val exerciseTypes = listOf("C", "B", "R", "M")

suspend fun ApplicationCall.displayWorkout(id: Int) {
    suspendTransaction {
        val workout = Workout.get(id)
        val workoutExercises = WorkoutExercise.all().filter {it.workout==workout}.sortedBy {it.order}.toList() // Sorting by order
        val types = mutableSetOf<String>() // Set used as a data structure with no duplicate values
        for (workoutExercise in workoutExercises) {
            // Append exercise type to list
            types.add(exerciseTypes[workoutExercise.exercise.type])
        }

        val wo = WorkoutObject(
            workout = workout, // Containing the workout content (name, desc, type)
            exercises = workoutExercises, // Containing the workout exercise content (workout, exercise, order, sets, reps, duration)
            types = types // Containing the types of exercises within the workout
        )

        respond(PebbleContent("view_workout.peb", mapOf("workout" to wo, "currentPage" to "workouts")))
    }
}

suspend fun ApplicationCall.displayWorkouts() {
    // Goal of this function:
    //  To create a workout object consisting of:
    //   workouts = workouts
    //   workoutTypes = workoutTypes (which exercise types in each workout?)
    suspendTransaction {
        val userSession = sessions.get<UserSession>()
        if (userSession != null) {
            val workoutObjects = mutableListOf<WorkoutObject>()
            val workouts = Workout.all().filter {it.user?.id.toString() == userSession.id || it.user == null}.toList()
            // Iterate through all found workouts and find what types are within them
            for (workout in workouts) {
                val workoutExercises = WorkoutExercise.all().filter {it.workout==workout}.toList()
                val types = mutableSetOf<String>() // We use sets as an easy implementation of no-duplicates
                for (workoutExercise in workoutExercises) {
                    // Append exercise type to list
                    types.add(exerciseTypes[workoutExercise.exercise.type])
                }
                workoutObjects.add(WorkoutObject(workout=workout, exercises = workoutExercises, types=types))

            }
            // After workoutTypes has been fully formed, each workout has a corresponding type
            respond(PebbleContent("view_workouts.peb", mapOf("workouts" to workoutObjects, "currentPage" to "workouts")))
        }
        else {
            respond(PebbleContent("landing.peb", mapOf("currentPage" to "landing")))
        }
    }
}

suspend fun ApplicationCall.searchWorkouts() {
    suspendTransaction {
        val userSession = sessions.get<UserSession>()
        if (userSession != null) {
            val workoutObjects = mutableListOf<WorkoutObject>()
            val search = parameters["search"]

            if (search == null) {
                respond(HttpStatusCode.BadRequest)
            }
            else {
                val workouts = Workout.all().filter {it.user?.id.toString() == userSession.id || it.user == null}.sortedBy {it.name}.filter {it.name.contains(search, true)}.toList()
                // Iterate through all found workouts and find what types are within them
                for (workout in workouts) {
                    val workoutExercises = WorkoutExercise.all().filter {it.workout==workout}.toList()
                    val types = mutableSetOf<String>() // We use sets as an easy implementation of no-duplicates
                    for (workoutExercise in workoutExercises) {
                        // Append exercise type to list
                        types.add(exerciseTypes[workoutExercise.exercise.type])
                    }
                    workoutObjects.add(WorkoutObject(workout=workout, exercises = workoutExercises, types=types))

                }
                // After workoutTypes has been fully formed, each workout has a corresponding type
                respond(PebbleContent("view_workouts.peb", mapOf("workouts" to workoutObjects, "currentPage" to "workouts")))
            }
        }
        else {
            respond(PebbleContent("landing.peb", mapOf("currentPage" to "landing")))
        }
    }
}

// Class definition for workout session exercises to be passed in the respondTemplate calls for functions linking to current-workout (below)
data class WorkoutSessionExerciseObject (
    var wsExercise: WorkoutSessionExercise,
    var sets: List<WorkoutSessionSet>,
)

suspend fun ApplicationCall.startNewWorkout() {
    suspendTransaction {
        val userSession = sessions.get<UserSession>()
        if (userSession != null) {
            // First, create the WorkoutSession instance
            val userEntity = User.all().first() {it.id.toString() == userSession.id}
            val workoutSessionID = WorkoutSession.new { user = userEntity }.id.toString() // Only need to pass user as all other attributes have default values
            // Second, set the CurrentWorkoutSession values for use of page generation
            sessions.set(CurrentWorkoutSession(workoutSessionID, userSession.id))
            // Third, we must get a list of all exercises in the database
            val exercises = Exercise.all().sortedBy {it.name}.toList()
            // Finally, since this is a new workout, we currently have no values to pass, so we only pass CurrentPage and exercises
            println("Created WorkoutSession and set CurrentWorkoutSession")
            println("CurrentWorkoutSession:")
            println(workoutSessionID)
            println(userSession.id)
            respond(PebbleContent("current_workout.peb", mapOf("currentPage" to "current-workout", "exercises" to exercises)))
        }
        else {
            respond(PebbleContent("landing.peb", mapOf("currentPage" to "landing")))
        }
    }
}

suspend fun ApplicationCall.continueWorkout() {
    suspendTransaction {
        val userSession = sessions.get<UserSession>()
        val workoutSessionID = sessions.get<CurrentWorkoutSession>()?.workoutSessionID
        if (userSession != null && workoutSessionID != null) {
            val ws = WorkoutSession.all().firstOrNull { it.id.toString() == workoutSessionID }
            val exercises = Exercise.all().sortedBy {it.name}.toList()
            println(exercises)
            if (ws != null) {
                val wseo = getListOfWorkoutSessionExercises(ws)
                respond(
                    PebbleContent(
                        "current_workout.peb",
                        mapOf("currentPage" to "current-workout", "exercises" to exercises, "workoutSessionExerciseObjects" to wseo)
                    )
                )
            }
            else {
                println("WorkoutSession is null")
                respond(PebbleContent("landing.peb", mapOf("currentPage" to "landing")))
            }
        }
        else {
            if (userSession == null) {
                println("UserSession is null")
            }
            if (workoutSessionID == null) {
                println("workoutSessionID is null")
            }
            respond(PebbleContent("landing.peb", mapOf("currentPage" to "landing")))
        }
    }
}

suspend fun ApplicationCall.addExercise(exerciseChoice: String) {
    suspendTransaction {
        // Get workoutSessionID
        val workoutSessionID = sessions.get<CurrentWorkoutSession>()?.workoutSessionID
        if (workoutSessionID != null) {
            // Get WorkoutSession and Exercise
            val ws = WorkoutSession.all().firstOrNull { it.id.toString() == workoutSessionID }
            val ex = Exercise.all().firstOrNull { it.id.toString() == exerciseChoice }
            println(ws)
            println(ex)
            if (ws != null && ex != null) {
                // Figure out the order below
                val wsExerciseList = WorkoutSessionExercise.all().sortedBy { it.workoutSession == ws }.toList()
                var maxOrder = 1
                for (x in wsExerciseList) {
                    if (x.order > maxOrder) {
                        maxOrder = x.order
                    }
                }
                // Create an instance of a  WorkoutSessionExercise
                WorkoutSessionExercise.new {
                    workoutSession = ws
                    exercise = ex
                    order = maxOrder
                }
                println("Added WorkoutSessionExercise")
                val exercises = Exercise.all().sortedBy { it.name }.toList()
                // Create list of WorkoutSessionExerciseObject to pass as a collection
                val wseo = getListOfWorkoutSessionExercises(ws)
                respond(
                    PebbleContent(
                        "current_workout.peb",
                        mapOf("currentPage" to "current-workout", "exercises" to exercises, "workoutSessionExerciseObjects" to wseo)
                    )
                )
            }
            else {
                println("BAD REQUEST IN addExercise()")
                respond(HttpStatusCode.BadRequest)
            }
        }
    }
}