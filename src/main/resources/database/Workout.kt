// Workout entity, mapping onto Workout table

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class Workout(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Workout>(WorkoutTable)

    var exercise by Exercise referencedOn WorkoutTable.exercise
    var name by WorkoutTable.name
    var desc by WorkoutTable.desc

    override fun toString() = name
}