// WorkoutSession entity, mapping onto WorkoutSessions

package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class WorkoutSession(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WorkoutExercise>(WorkoutSessions)

    var user by User referencedOn WorkoutSessions.user
    var startedAt by WorkoutSessions.startedAt
    var endedAt by WorkoutSessions.endedAt
    var complete by  WorkoutSessions.complete
}