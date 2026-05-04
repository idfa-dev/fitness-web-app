// WorkoutSessionSet entity, mapping onto WorkoutSessionSets

package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class WorkoutSessionSet(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WorkoutSessionSet>(WorkoutSessionSets)

    var workoutSessionExercise by WorkoutSessionExercise referencedOn WorkoutSessionSets.workoutSessionExercise
    var reps by WorkoutSessionSets.reps
    var weight by WorkoutSessionSets.weight
}