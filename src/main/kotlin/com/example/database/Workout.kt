// Workout entity, mapping onto Workouts

package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class Workout(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Workout>(Workouts)

    var user by User optionalReferencedOn Workouts.user
    var name by Workouts.name
    var desc by Workouts.desc
    var type by Workouts.type
    var favourite by Workouts.favourite

    override fun toString() = name
}