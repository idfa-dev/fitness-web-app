// Table schema for PT

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

MAX_VARCHAR_LENGTH = 255

object PTs : IntIdTable {
    val client = reference("client_id", ClientTable, ReferenceOption.CASCADE)
    val username = varchar("username", MAX_VARCHAR_LENGTH)
    val password = varchar("password", MAX_VARCHAR_LENGTH)
    val email = varchar("email", MAX_VARCHAR_LENGTH)
    val fname = varchar("fname", MAX_VARCHAR_LENGTH)
    val height = float("height")
    val weight = float("weight")
    val dob = varchar("dob", 10) // length 10 for dob DD-MM-YYYY
    val sex = varchar("sex", MAX_VARCHAR_LENGTH)
}