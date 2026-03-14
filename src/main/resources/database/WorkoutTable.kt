// Table schema for WorkoutTable

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

MAX_VARCHAR_LENGTH = 255

class WorkoutsTable : IntIdTable {
    val exercise = reference("exercise_id", ExerciseTable, ReferenceOption.CASCADE)
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val desc = varchar("desc", MAX_VARCHAR_LENGTH)
}
