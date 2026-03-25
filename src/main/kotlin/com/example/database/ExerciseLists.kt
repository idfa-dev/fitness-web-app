// Table schema for ExerciseLists

package com.example.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object ExerciseLists : IntIdTable("exerciselists") {
    val workout = reference("workout_id", Workouts, ReferenceOption.CASCADE)
    val exercise = reference("exercise_id", Exercises, ReferenceOption.CASCADE)
    val order
    val sets
    val reps
    val duration

}