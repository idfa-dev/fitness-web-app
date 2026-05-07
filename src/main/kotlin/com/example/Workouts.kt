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
import com.example.database.WorkoutSessions.endedAt
import com.example.database.getListOfWorkoutSessionExercises
import com.example.database.getUserIdByUsername
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.pebble.PebbleContent
import io.ktor.server.pebble.respondTemplate
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.sessions.*
import io.pebbletemplates.pebble.template.PebbleTemplate
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

        respond(PebbleContent("workouts/view_workout.peb", mapOf("workout" to wo, "currentPage" to "workouts")))
    }
}

suspend fun ApplicationCall.getListOfWorkoutObjects(): MutableList<WorkoutObject> {
    val workoutObjects = mutableListOf<WorkoutObject>()
    suspendTransaction {
        val userSession = sessions.get<UserSession>()
        if (userSession != null) {
            val workouts = Workout.all().filter { it.user?.id?.value.toString() == userSession.id || it.user == null }.toList()
            // Iterate through all found workouts and find what types are within them
            for (workout in workouts) {
                val workoutExercises = WorkoutExercise.all().filter { it.workout == workout }.toList()
                val types = mutableSetOf<String>() // We use sets as an easy implementation of no-duplicates
                for (workoutExercise in workoutExercises) {
                    // Append exercise type to list
                    types.add(exerciseTypes[workoutExercise.exercise.type])
                }
                workoutObjects.add(WorkoutObject(workout = workout, exercises = workoutExercises, types = types))
            }
        }
        else {
            respond(PebbleContent("landing/auth/landing.peb", mapOf("currentPage" to "landing")))
        }
    }
    if (workoutObjects.isEmpty()) {
        println("No workout objects could be made")
    }
    return workoutObjects
}

suspend fun ApplicationCall.displayWorkouts(workoutID: Int = 0, from: String = "workouts") {
    suspendTransaction {
        if (workoutID != 0) {
            // Change favourite value of workout
            Workout.findByIdAndUpdate(workoutID) {
                it.favourite = !it.favourite
            }
        }
        val workoutObjects  = getListOfWorkoutObjects()
        respond(PebbleContent("workouts/view_workouts.peb", mapOf("currentPage" to from, "workouts" to workoutObjects)))
    }
}

suspend fun ApplicationCall.searchWorkouts() {
    suspendTransaction {

        val userSession = sessions.get<UserSession>()
        if (userSession == null) {
            respond(PebbleContent("landing/auth/landing.peb", mapOf("currentPage" to "landing")))
            return@suspendTransaction
        }

        val currentPage = parameters["currentPage"] ?: "workouts"
        val search = parameters["search"]
        val selectedTypes = parameters.getAll("type") ?: emptyList()
        val favouriteOnly = parameters["favourite"] == "true"

        val workoutObjects = mutableListOf<WorkoutObject>()

        val workouts = Workout.all()
            .filter { it.user?.id.toString() == userSession.id || it.user == null }
            .toList()
            .filter { workout ->

                val matchesSearch =
                    search.isNullOrBlank() ||
                    workout.name.contains(search, ignoreCase = true)

                val workoutTypes = WorkoutExercise.all()
                    .filter { it.workout == workout }
                    .map { exerciseTypes[it.exercise.type] }

                val matchesType =
                    selectedTypes.isEmpty() ||
                    workoutTypes.any { it in selectedTypes }

                val matchesFavourite =
                    !favouriteOnly || workout.favourite

                matchesSearch && matchesType && matchesFavourite
            }

        for (workout in workouts) {

            val workoutExercises = WorkoutExercise.all()
                .filter { it.workout == workout }
                .toList()

            val types = workoutExercises
                .map { exerciseTypes[it.exercise.type] }
                .toSet()

            workoutObjects.add(
                WorkoutObject(
                    workout = workout,
                    exercises = workoutExercises,
                    types = types
                )
            )
        }
        respond(PebbleContent("workouts/view_workouts.peb", mapOf("workouts" to workoutObjects, "currentPage" to currentPage)))
    }
}

// Class definition for WorkoutSessionExerciseObject to be passed in the respondTemplate calls for functions linking to current-workout (below)
data class WorkoutSessionExerciseObject (
    var wsExercise: WorkoutSessionExercise,
    var sets: List<WorkoutSessionSet>,
)

// Class definition for LastWeightObject (to display last known weight of exercises as placeholders)
data class LastWeightObject (
    var exercise: Exercise,
    var weight: Float,
    var reps: Int
)

suspend fun ApplicationCall.getListOfLastWeightObjects(user: User): MutableList<LastWeightObject> {
    // Assemble list of last weight objects
    val lastWeightObjects = mutableListOf<LastWeightObject>()
    suspendTransaction {
        // Find all WorkoutSessions of user
        val workoutSessions = WorkoutSession.all().filter {it.user == user}.toList()
        for (ws in workoutSessions) {
            val workoutSessionExercises = WorkoutSessionExercise.all().filter {it.workoutSession == ws}.toList()
            for (wse in workoutSessionExercises) {
                val lastWorkoutSessionSet = WorkoutSessionSet.all().lastOrNull { it.workoutSessionExercise == wse }
                if (lastWorkoutSessionSet != null) {
                    val lastWeight = lastWorkoutSessionSet.weight
                    val lastReps = lastWorkoutSessionSet.reps
                    if (lastWeight != null) {
                        val lwo = LastWeightObject (
                            exercise = wse.exercise,
                            weight = lastWeight,
                            reps = lastReps
                        )
                        var replaced = false
                        for (oldLwo in lastWeightObjects) {
                            if (oldLwo.exercise == lwo.exercise) {
                                lastWeightObjects.replaceAll {if (it.exercise == lwo.exercise) lwo else it}
                                replaced = true
                            }
                        }
                        if (!replaced) {
                            lastWeightObjects.add(lwo)
                        }
                    }
                }
            }
        }
    }
    return lastWeightObjects
}

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
            val lastWeightObjects = getListOfLastWeightObjects(userEntity)
            respond(PebbleContent("current_workout/current_workout.peb", mapOf("currentPage" to "current-workout", "exercises" to exercises, "lastWeightObjects" to lastWeightObjects)))
        }
        else {
            respond(PebbleContent("landing/auth/landing.peb", mapOf("currentPage" to "landing")))
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
                val user = User.all().firstOrNull() {it.id.toString() == userSession.id}
                if (user != null) {
                    val lastWeightObjects = getListOfLastWeightObjects(user)
                    println("LAST WEIGHT OBJECTS COUNT")
                    println(lastWeightObjects.count())
                    for (lwo in lastWeightObjects) {
                        println("lwo exercise: ${lwo.exercise}, lwo exercise weight: ${lwo.weight}, lwo exercise reps: ${lwo.reps}")
                    }
                    respond(PebbleContent("current_workout/current_workout.peb", mapOf("currentPage" to "current-workout", "exercises" to exercises, "workoutSessionExerciseObjects" to wseo, "lastWeightObjects" to lastWeightObjects)))
                }
                else {
                    println("User not found")
                }
            }
            else {
                println("WorkoutSession is null")
                respond(PebbleContent("landing/auth/landing.peb", mapOf("currentPage" to "landing")))
            }
        }
        else {
            if (userSession == null) {
                println("UserSession is null")
            }
            if (workoutSessionID == null) {
                println("workoutSessionID is null")
            }
            respond(PebbleContent("landing/auth/landing.peb", mapOf("currentPage" to "landing")))
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
                println("Successfully added WorkoutSessionExercise")
                continueWorkout()
            }
            else {
                println("BAD REQUEST IN addExercise()")
                respond(HttpStatusCode.BadRequest)
            }
        }
    }
}

suspend fun ApplicationCall.addSet(workoutSessionExerciseID: Int, inputReps: Int, inputWeight: Float) {
    suspendTransaction {
        // Get workoutSessionExercise from id
        val wse = WorkoutSessionExercise.all().firstOrNull {it.id.toString() == workoutSessionExerciseID.toString()}
        if (wse != null) {
            WorkoutSessionSet.new {
                workoutSessionExercise = wse
                reps = inputReps
                weight = inputWeight
            }
            println(println("Successfully added WorkoutSessionSet"))
            continueWorkout()
        }
        else {
            println("wse is null")
        }
    }
}

suspend fun ApplicationCall.removeSet(wssid: Int) {
    suspendTransaction {
        val set = WorkoutSessionSet.all().firstOrNull {it.id.toString() == wssid.toString()}
        if (set != null) {
            set.delete()
            println("Successfully deleted WorkoutSessionSet with id=$wssid")
            continueWorkout()
        }
        else {
            println("WorkoutSessionSet with id=$wssid not found")
        }
    }
}

suspend fun ApplicationCall.removeExercise(wseid: Int) {
    suspendTransaction {
        val exercise = WorkoutSessionExercise.all().firstOrNull {it.id.toString() == wseid.toString()}
        if (exercise != null) {
            exercise.delete()
            println("Successfully deleted WorkoutSessionExercise with id=$wseid")
            continueWorkout()
        }
        else {
            println("WorkoutSessionExercise with id=$wseid not found")
        }
    }
}

suspend fun ApplicationCall.endWorkout() {
    suspendTransaction {
        val workoutSessionID = sessions.get<CurrentWorkoutSession>()?.workoutSessionID?.toIntOrNull()
        if (workoutSessionID != null) {
            val workoutSession = WorkoutSession.findById(workoutSessionID)
            if (workoutSession != null) {
                // Handle case of empty workout
                val we = WorkoutSessionExercise.all().firstOrNull {it.workoutSession == workoutSession}
                if (we != null) {
                    val ws = WorkoutSession.findByIdAndUpdate(workoutSessionID) {
                        it.endedAt = Instant.now()
                        it.complete = true
                    }
                    println("Successfully completed WorkoutSession with id=$workoutSessionID")
                }
                else {
                    workoutSession.delete()
                    println("WorkoutSession with id: $workoutSessionID deleted due to being empty")
                }
                // Unset the current workout session
                sessions.clear<CurrentWorkoutSession>()
                respond(PebbleContent("current_workout/start_workout.peb", mapOf("currentPage" to "current-workout")))
            }
            else {
                println("WorkoutSession is null")
            }
        }
        else {
            println("WorkoutSessionID is null")
        }
    }
}

suspend fun ApplicationCall.selectWorkoutTemplate() {
    suspendTransaction {
        val workoutObjects = getListOfWorkoutObjects()
        respond(PebbleContent("workouts/view_workouts.peb", mapOf("currentPage" to "current-workout", "workouts" to workoutObjects)))
    }
}

suspend fun ApplicationCall.startNewTemplateWorkout(workoutID: Int) {
    // To do this, parameters["workout_template"] will contain a workout id, from here do something to make it work
    // 1) Create WorkoutSession
    // 2) Create WorkoutSessionExercise with workoutSession = WorkoutSession just created for all exercises in workout
    // 3) For now, leave it as this.
    suspendTransaction {
        val userSession = sessions.get<UserSession>()
        if (userSession != null) {
            // Validate that a workout exists with given workoutID
            val workout = Workout.all().firstOrNull { it.id.toString() == workoutID.toString() }
            if (workout != null) {
                // Create WorkoutSession
                val userEntity = User.all().first() { it.id.toString() == userSession.id }
                val newWorkoutSession = WorkoutSession.new { user = userEntity }
                val workoutSessionID = newWorkoutSession.id.value.toString()
                // Set the CurrentWorkoutSession values for use of page generation
                sessions.set(CurrentWorkoutSession(workoutSessionID, userSession.id))
                // Add all exercises in the workout as WorkoutSessionExercises
                val exercises = WorkoutExercise.all().filter {it.workout == workout}.toList()
                for (e in exercises) {
                    WorkoutSessionExercise.new {
                        workoutSession = newWorkoutSession
                        exercise = e.exercise
                        order = e.order
                    }
                }
                // Once all exercises are added, display the page
                continueWorkout()
            } else {
                println("Workout with id=$workoutID not found")
            }
        }
    }
}

// Class definition for PastWorkoutObject
data class PastWorkoutObject (
    var workoutSession: WorkoutSession,
    var workoutExercisesObject: List<PastWorkoutExerciseObject>,
    var length: String,
    var endedAt: String
)

// Class definition for PastWorkoutExerciseObject for use in PastWorkoutObject
data class PastWorkoutExerciseObject (
    val exercise: WorkoutSessionExercise,
    val sets: List<WorkoutSessionSet>
)

suspend fun ApplicationCall.displayPastWorkouts() {
    // First, get all WorkoutSessions where user == user and completed == true
    // Next, we must create a PastWorkoutObject for each workout session
    // To do this we need to also create PastWorkoutExerciseObjects for each exercise in the workoutSession
    //      Find all exercises part of the workout session
    //      Find all sets part of the workout session
    // Also calculate length of workouts and pass that too
    suspendTransaction {
        val pastWorkouts = mutableListOf<PastWorkoutObject>()
        val userSession = sessions.get<UserSession>()
        if (userSession != null) {
            val userID = userSession.id.toIntOrNull()
            if (userID != null) {
                val user = User.findById(userID)
                val workoutSessions = WorkoutSession.all().filter { it.user == user && it.complete }.toList()
                for (ws in workoutSessions) {
                    val workoutExercises = WorkoutSessionExercise.all().filter {it.workoutSession == ws}.toList()
                    val exercisesList = mutableListOf<PastWorkoutExerciseObject>()
                    for (we in workoutExercises) {
                        val workoutSets = WorkoutSessionSet.all().filter {it.workoutSessionExercise == we }.toList()
                        // Create a PastWorkoutExerciseObject for every exercise in we
                        val exerciseObject = PastWorkoutExerciseObject (
                            exercise = we,
                            sets = workoutSets
                        )
                        exercisesList.add(exerciseObject)
                    }
                    // Convert datetime for date completed to a readable format
                    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm").withZone(ZoneId.systemDefault())
                    val endedAt = formatter.format(ws.endedAt)
                    // Calculate length of workout
                    val duration = Duration.between(ws.startedAt, ws.endedAt)
                    val hours = duration.toHours()
                    val minutes = duration.minusHours(hours).toMinutes()
                    val seconds = duration.minusMinutes(minutes).toSeconds()
                    val length = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    // Create a PastWorkoutObject for every ws
                    val workoutObject = PastWorkoutObject (
                        workoutSession = ws,
                        workoutExercisesObject = exercisesList,
                        length = length,
                        endedAt = endedAt
                    )
                    pastWorkouts.add(workoutObject)
                }
                if (pastWorkouts.isNotEmpty()) {
                    respond(PebbleContent("workouts/view_past_workouts.peb", mapOf("currentPage" to "workouts", "pastWorkouts" to pastWorkouts)))
                }
                else {
                    val errMsg = "You have not completed any workouts yet"
                    respond(PebbleContent("workouts/workouts.peb", mapOf("currentPage" to "workouts", "errMsg" to errMsg)))
                }
            }
            else {
                respond(HttpStatusCode.Unauthorized)
            }
        }
        else {
            println("UserSession is null")
        }
    }
}