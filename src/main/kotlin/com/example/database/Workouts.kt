// Table schema for Workouts

package com.example.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Workouts : IntIdTable("workouts") {
    val user = optReference("user_id", Users, ReferenceOption.CASCADE)
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val desc = varchar("desc", MAX_VARCHAR_LENGTH)
    val type = integer("type")
    val favourite = bool("favourite").default(false)
}

/*
type

0 = Cardio
1 = Bodyweight
2 = Resistance
3 = Mixed
 */