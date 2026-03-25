// Table schema for Workouts

package com.example.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object Workouts : IntIdTable("workouts") {
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val desc = varchar("desc", MAX_VARCHAR_LENGTH)
}
