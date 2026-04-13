package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import com.example.database.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun Application.module() {
    
    install(ContentNegotiation) {
        json()
    }
    
    Database.connect("jdbc:h2:file:./data/fitness", driver = "org.h2.Driver") // Starts com.example.database connection (fitness = dbname)

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Users, PTs, Exercises, Clients, Workouts, UserWorkouts)

        if (User.all().empty()) {
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
        

        val c_ex_1 = Exercise.new {
            name = "treadmill"
            type = 0
            url = "G7QjU44eBvA"
            rating = 1
        }

        val c_ex_2 = Exercise.new {
            name = "rowing machine"
            type = 0
            url = "J1nf2Zfbazs"
            rating = 2
        }

        val bw_ex_1 = Exercise.new {
            name = "push ups"
            type = 1
            url = "IODxDxX7oi4"
            rating = 1
        }

        val bw_ex_2 = Exercise.new {
            name = "pull ups"
            type = 1
            url = "eGo4IYlbE5g"
            rating = 2
        }

        val r_ex_1 = Exercise.new {
            name = "barbell bench press"
            type = 2
            url = "gRVjAtPip0Y"
            rating = 2
        }

        val r_ex_2 = Exercise.new {
            name = "barbell squat"
            type = 2
            url = "my0tLDaWyDU"
            rating = 2
        }

        val m_ex_1 = Exercise.new {
            name = "farmer's walk"
            type = 3
            url = "NH7Xv-7NQNQ"
            rating = 3
        }

        val m_ex_2 = Exercise.new {
            name = "clean and jerk"
            type = 3
            url = "PjY1rH4_MOA"
            rating = 3
        }
        for (exercise in Exercise.all()) {
            println("Created exercise with id: ${exercise.id}, name: ${exercise.name}, type: ${exercise.type}, rating: ${exercise.rating}")
        }
    }

    configureRouting()
    configureTemplates()
}
