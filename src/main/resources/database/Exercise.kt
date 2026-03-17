// Exercise entity, mapping onto Exercise table

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class Exercise(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Exercise>(ExerciseTable)

    var name by ExerciseTable.name
    var type by ExerciseTable.type
    var url by ExerciseTable.url
    var rating by ExerciseTable.rating

    override fun toString() = name
}