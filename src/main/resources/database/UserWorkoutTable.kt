// Table schema for UserWorkout

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

MAX_VARCHAR_LENGTH = 255

object UserWorkoutTable : IntIdTable {
    val user = reference("user_id", UserTable, ReferenceOption.CASCADE)
    val exercise = reference("exercise_id", ExerciseTable, ReferenceOption.CASCADE)
}