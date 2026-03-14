// UserWorkout entity, mapping onto UserWorkout table

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class UserWorkout(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserWorkout>(UserWorkoutTable)

    var user by User referencedOn UserWorkoutTable.user
    var exercise by Exercise referencedOn UserWorkoutTable.exercise
}