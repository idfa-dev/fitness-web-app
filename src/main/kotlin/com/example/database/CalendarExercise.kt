package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class CalendarExercise(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CalendarExercise>(CalendarExercises)

    var user by User referencedOn CalendarExercises.user
    var name by CalendarExercises.name
    var date by CalendarExercises.date

    var timeTaken by CalendarExercises.timeTaken
    var calories by CalendarExercises.calories
    var distance by CalendarExercises.distance
    var machine by CalendarExercises.machine
    var weight by CalendarExercises.weight
    var setsReps by CalendarExercises.setsReps
    var type by CalendarExercises.type
}