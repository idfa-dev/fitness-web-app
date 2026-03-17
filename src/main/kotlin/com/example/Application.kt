package com.example

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import com.example.database.*
import org.jetbrains.exposed.v1.core.StdOutSqlLogger

fun Application.module() {
    Database.connect("jdbc:h2:mem:fitness", driver = "org.h2.Driver") // Starts com.example.database connection (fitness = dbname)

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Users)
        SchemaUtils.create(PTs)
        SchemaUtils.create(Exercises)
        SchemaUtils.create(Clients)
        SchemaUtils.create(Workouts)
        SchemaUtils.create(UserWorkouts)

        val user1 = User.new {
            type = 0
            username = "regulardude123"
            password = "regular"
            email = "regular@gmail.com"
            fname = "Reggie"
            height = 176.50f
            weight = 76.20f
            dob = "03-03-2004"
            sex = "male"
        }
        println("Created User 1 with id = ${user1.id} and username = ${user1.username}")
    }

    configureRouting()
    configureTemplates()
}
