// Table schema for WorkoutSessionExercises

package com.example.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object WorkoutSessionExercises : IntIdTable("workout_session_exercises") {
    val workoutSession = reference("workout_session_id", WorkoutSessions, ReferenceOption.CASCADE)
    val exercise = reference("exercise_id", Exercises, ReferenceOption.CASCADE)
    val order = integer("order")
}