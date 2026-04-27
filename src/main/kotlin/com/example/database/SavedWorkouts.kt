// Table schema for SavedWorkouts

package com.example.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object SavedWorkouts : IntIdTable("savedworkouts") {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val workout = reference("workout_id", Workouts, ReferenceOption.CASCADE)
}