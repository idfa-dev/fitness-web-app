// WorkoutExercise entity, mapping onto WorkoutExercises

package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class WorkoutExercise(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WorkoutExercise>(WorkoutExercises)

    var workout by Workout referencedOn WorkoutExercises.workout
    var exercise by Exercise referencedOn WorkoutExercises.exercise
    var order by WorkoutExercises.order
    var sets by WorkoutExercises.sets
    var reps by WorkoutExercises.reps
    var duration by WorkoutExercises.duration
}