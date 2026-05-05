// PT entity, mapping onto PTs

package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class PT(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PT>(PTs)

    var client by PTs.client
    var username by PTs.username
    var password by PTs.password
    var email by PTs.email
    var fname by PTs.fname
    var height by PTs.height
    var weight by PTs.weight
    var dob by PTs.dob
    var sex by PTs.sex

    override fun toString() = username
}