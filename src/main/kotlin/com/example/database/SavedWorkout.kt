// SavedWorkout entity, mapping onto UserWorkouts

package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class SavedWorkout(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SavedWorkout>(SavedWorkouts)

    var user by User referencedOn SavedWorkouts.user
    var workout by Workout referencedOn SavedWorkouts.workout
}