// Table schema for User

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

MAX_VARCHAR_LENGTH = 255

object Users : IntIdTable("users") {
    val type = integer("type", 1)
    val username = varchar("username", MAX_VARCHAR_LENGTH)
    val password = varchar("password", MAX_VARCHAR_LENGTH)
    val email = varchar("email", MAX_VARCHAR_LENGTH)
    val fname = varchar("fname", MAX_VARCHAR_LENGTH)
    val height = float("height")
    val weight = float("weight")
    val dob = varchar("dob", 10) // length 10 for dob DD-MM-YYYY
    val sex = varchar("sex", MAX_VARCHAR_LENGTH)
}

/*
user type

0 = Regular
1 = Competitive

 */