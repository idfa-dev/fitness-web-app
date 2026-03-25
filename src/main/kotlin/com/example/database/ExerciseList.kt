// ExerciseList entity, mapping onto ExerciseLists

package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class ExerciseList(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExerciseList>(ExerciseLists)
}