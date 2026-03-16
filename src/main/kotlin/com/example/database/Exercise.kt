// Exercise entity, mapping onto Exercise table

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class Exercise(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Exercise>(Exercises)

    var name by Exercises.name
    var type by Exercises.type
    var url by Exercises.url
    var rating by Exercises.rating

    override fun toString() = name
}