package com.example.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.javatime.date

object Competitions : IntIdTable() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 255)
    val date = date("date")
    val time = varchar("time", 50).nullable()
    val distance = double("distance").nullable()
    val finishTime = varchar("finish_time", 50).nullable()
    val types = varchar("types", 255).nullable()
}