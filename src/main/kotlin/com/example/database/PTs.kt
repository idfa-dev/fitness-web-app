// Table schema for PTs

package com.example.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object PTs : IntIdTable("pts") {
<<<<<<< HEAD
    val client = optReference("client_id", Clients, ReferenceOption.CASCADE)
=======
>>>>>>> eddb66e50be9d8a00da28a11658beb5efff4099d
    val username = varchar("username", MAX_VARCHAR_LENGTH)
    val password = varchar("password", MAX_VARCHAR_LENGTH)
    val email = varchar("email", MAX_VARCHAR_LENGTH)
    val fname = varchar("fname", MAX_VARCHAR_LENGTH)
    val height = float("height")
    val weight = float("weight")
    val dob = varchar("dob", 10) // length 10 for dob DD-MM-YYYY
    val sex = varchar("sex", MAX_VARCHAR_LENGTH)
}