// PT entity, mapping onto PT table

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class PT(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PT>(PTTable)

    var client by Client referencedOn PTTable.client
    var username by UserTable.username
    var password by UserTable.password
    var email by UserTable.email
    var fname by UserTable.fname
    var height by UserTable.height
    var weight by UserTable.float
    var dob by UserTable.dob
    var sex by UserTable.sex

    override fun toString() = username
}