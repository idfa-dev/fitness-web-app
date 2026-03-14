// Table schema for Exercise

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

MAX_VARCHAR_LENGTH = 255

object ExerciseTable : IntIdTable {
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val type = integer("type", 1)
    val url = varchar("url", MAX_VARCHAR_LENGTH)
    val rating = integer("rating", 1)
}

/*
type

0 = Cardio
1 = Bodyweight
2 = Resistance
3 = Mixed
 */