// Table schema for UserWorkouts

package com.example.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object UserWorkouts : IntIdTable("userworkouts") {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val desc = varchar("desc", MAX_VARCHAR_LENGTH)
}