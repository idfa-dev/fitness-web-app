// Table schema for UserWorkout

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

MAX_VARCHAR_LENGTH = 255

object UserWorkouts : IntIdTable("userworkouts") {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val exercise = reference("exercise_id", Exercices, ReferenceOption.CASCADE)
}