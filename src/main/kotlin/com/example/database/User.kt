// User entity, mapping onto Users

package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var type by Users.type
    var username by Users.username
    var password by Users.password
    var email by Users.email
    var fname by Users.fname
    var height by Users.height
    var weight by Users.weight
    var dob by Users.dob
    var sex by Users.sex

    override fun toString(): String {
        return "User(id=$id, type=$type, username=$username)"
    }
}