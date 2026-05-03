// Table schema for WorkoutSessionSets

package com.example.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object WorkoutSessionSets : IntIdTable("workout_session_sets") {
    val workoutSessionExercise = reference("workout_session_exercise_id", WorkoutSessionExercises, ReferenceOption.CASCADE)
    val reps = integer("reps")
    val weight = float("weight").nullable()
}