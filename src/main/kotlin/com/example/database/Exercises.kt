// Table schema for Exercise

package com.example.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Exercises : IntIdTable("exercises") {
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val type = integer("type")
    val url = varchar("url", MAX_VARCHAR_LENGTH)
    val rating = integer("rating")
}

/*
type

0 = Cardio
1 = Bodyweight
2 = Resistance
3 = Mixed
 */