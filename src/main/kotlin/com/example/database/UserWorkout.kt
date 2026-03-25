// UserWorkout entity, mapping onto UserWorkouts

package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class UserWorkout(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserWorkout>(UserWorkouts)

    var user by User referencedOn UserWorkouts.user
    var exerciselist by ExerciseList referencedOn UserWorkouts.exerciselist
    var name by UserWorkouts.name
    var desc by UserWorkouts.desc
}