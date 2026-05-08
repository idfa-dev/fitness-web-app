package com.example.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.javatime.date

object CalendarExercises : IntIdTable("calendar_exercises") {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 255)
    val date = date("date")

    val timeTaken = varchar("time_taken", 50).nullable()
    val calories = integer("calories").nullable()
    val distance = double("distance").nullable()
    val machine = varchar("machine", 100).nullable()
    val weight = double("weight").nullable()
    val setsReps = varchar("sets_reps", 50).nullable()
    val type = varchar("type", 50).nullable()
}