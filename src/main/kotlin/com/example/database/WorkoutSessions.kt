// Table schema for WorkoutSessions

package com.example.database

import java.time.Instant
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object WorkoutSessions : IntIdTable("workout_sessions") {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val startedAt = timestamp("started_at").clientDefault { Instant.now() }
    val endedAt = timestamp("ended_at").nullable()
    val complete = bool("complete").default(false)
}