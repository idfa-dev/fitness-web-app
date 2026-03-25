package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import com.example.database.*

fun Application.module() {
    Database.connect("jdbc:h2:mem:fitness;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver") // Starts com.example.database connection (fitness = dbname)

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Users)
        SchemaUtils.create(PTs)
        SchemaUtils.create(Exercises)
        SchemaUtils.create(Clients)
        SchemaUtils.create(Workouts)
        SchemaUtils.create(UserWorkouts)

        // User insertions
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

        // Exercise insertions
        val c_ex_1 = Exercise.new {
            name = "Treadmill"
            type = 0
            url = "G7QjU44eBvA"
            rating = 1
        }

        val c_ex_2 = Exercise.new {
            name = "Rowing machine"
            type = 0
            url = "J1nf2Zfbazs"
            rating = 2
        }

        val bw_ex_1 = Exercise.new {
            name = "Push ups"
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
            name = "Barbell bench press"
            type = 2
            url = "gRVjAtPip0Y"
            rating = 2
        }

        val r_ex_2 = Exercise.new {
            name = "Barbell squat"
            type = 2
            url = "my0tLDaWyDU"
            rating = 2
        }

        val m_ex_1 = Exercise.new {
            name = "Farmer's walk"
            type = 3
            url = "NH7Xv-7NQNQ"
            rating = 3
        }

        val m_ex_2 = Exercise.new {
            name = "Clean and jerk"
            type = 3
            url = "PjY1rH4_MOA"
            rating = 3
        }
        for (exercise in Exercise.all()) {
            println("Created exercise with id: ${exercise.id}, name: ${exercise.name}, type: ${exercise.type}, rating: ${exercise.rating}")
        }

        // Workout insertions
        val w_1 = Workout.new {
            name = "Push and pull"
            desc = "A small workout that can be done from the comfort of your home."
            type = 1
        }

        val w_2 = Workout.new {
            name = "Tough day"
            desc = "A workout to put your skills in the gym to the test."
            type = 3
        }

        val w_3 = Workout.new {
            name = "Simple running on the treadmill"
            desc = "Using the treadmill to do a simple running exercise."
            type = 0
        }

        val w_4 = Workout.new {
            name = "A day out at sea"
            desc = "Using the rowing machine to do some full-body cardio."
            type = 0
        }
    }

    configureRouting()
    configureTemplates()
}
