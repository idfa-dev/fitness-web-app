// Table schema for Users

package com.example.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Users : IntIdTable("users") {
    val type = integer("type")
    val username = varchar("username", MAX_VARCHAR_LENGTH)
    val password = varchar("password", MAX_VARCHAR_LENGTH)
    val email = varchar("email", MAX_VARCHAR_LENGTH)
    val fname = varchar("fname", MAX_VARCHAR_LENGTH)
    val height = float("height")
    val weight = float("weight")
    val age = integer("age").nullable()
    val dob = varchar("dob", 10) // length 10 for dob DD-MM-YYYY
    val sex = varchar("sex", MAX_VARCHAR_LENGTH)
}

/*
user type

1 = Casual
2 = Competitive
3 = PT ( only for sign-up, 3 does not exist as a type in DB )

 */