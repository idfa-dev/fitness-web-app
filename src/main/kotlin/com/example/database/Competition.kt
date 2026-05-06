package com.example.database

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class Competition(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Competition>(Competitions)

    var user by User referencedOn Competitions.user
    var name by Competitions.name
    var date by Competitions.date
    var time by Competitions.time
    var distance by Competitions.distance
    var finishTime by Competitions.finishTime
    var types by Competitions.types
}