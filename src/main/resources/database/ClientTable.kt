// Table schema for ClientTable

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

MAX_VARCHAR_LENGTH = 255

object ClientTable : IntIdTable {
    val user = reference("user_id", UserTable, ReferenceOption.CASCADE)
    val pt = reference("pt_id", PTTable, ReferenceOption.CASCADE)
}