// Table schema for WorkoutExercises

package com.example.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object WorkoutExercises : IntIdTable("workoutexercises") {
    val workout = reference("workout_id", Workouts, ReferenceOption.CASCADE)
    val exercise = reference("exercise_id", Exercises, ReferenceOption.CASCADE)
    val order = integer("order")
    val sets = integer("sets")
    val reps = integer("reps")
    val duration = float("duration")
}