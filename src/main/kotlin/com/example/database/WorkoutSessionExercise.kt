// WorkoutSessionExercise entity, mapping onto WorkoutSessionExercises

package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class WorkoutSessionExercise(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WorkoutSessionExercise>(WorkoutSessionExercises)

    var workoutSession by WorkoutSession referencedOn WorkoutSessionExercises.workoutSession
    var exercise by Exercise referencedOn WorkoutSessionExercises.exercise
    var order by WorkoutSessionExercises.order
}