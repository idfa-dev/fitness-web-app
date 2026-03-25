// Table schema for UserWorkouts

package com.example.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object UserWorkouts : IntIdTable("userworkouts") {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val exercise = reference("exercise_id", Exercises, ReferenceOption.CASCADE)
}