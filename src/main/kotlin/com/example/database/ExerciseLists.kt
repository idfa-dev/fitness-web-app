// Table schema for ExerciseLists

package com.example.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object ExerciseLists : IntIdTable("exerciselists") {
    val workout = reference("workout_id", Workouts, ReferenceOption.CASCADE)
    val exercise = reference("exercise_id", Exercises, ReferenceOption.CASCADE)
    val order = integer("order")
    val sets = integer("sets")
    val reps = integer("reps")
    val duration = float("duration")
}