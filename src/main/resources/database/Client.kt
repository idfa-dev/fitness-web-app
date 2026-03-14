// Client entity, mapping onto Client table

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class Client(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Client>(ClientTable)

    var user by User referencedOn PTTable.user
    var pt by PT referencedOn PTTable.pt
}