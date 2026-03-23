package com.example

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import com.example.database.*
import org.jetbrains.exposed.v1.core.StdOutSqlLogger

fun Application.module() {

    org.h2.tools.Server.createWebServer("-web", "-webPort", "8082").start() //View H2 database at http://localhost:8082

    Database.connect("jdbc:h2:./test", driver = "org.h2.Driver") // Database now changed to a permanent file
    
    //It kinda adds the same entry every time its run, I should probably change that but I'm a bit lazy rn ( I.F. )

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

        val treadmill = Exercise.new {
            name = "treadmill"
            type = 0
            url = "https://www.youtube.com/watch?v=G7QjU44eBvA"
            rating = 1
        }
        println("Created exercise: name=${treadmill.name}, type=${treadmill.type}, url=${treadmill.url}, rating=${treadmill.rating}")
    }

    configureRouting()
    configureTemplates()
}
