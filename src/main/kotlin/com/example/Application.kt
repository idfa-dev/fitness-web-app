package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import com.example.database.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.sessions.*

import org.h2.tools.Server //db viewer thing

fun Application.module() {

    install(ContentNegotiation) {
        json()
    }

    Database.connect("jdbc:h2:mem:fitness;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver") // Starts com.example.database connection (fitness = dbname)

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Users, PTs, Exercises, Clients, Workouts, UserWorkouts, WorkoutExercises, SavedWorkouts)

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

        WorkoutExercise.all().forEach { it.delete() }
        Workout.all().forEach { it.delete() }
        Exercise.all().forEach { it.delete() }

        println("Seeding exercises...")

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
            name = "Pull ups"
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

        val w_5 = Workout.new {
            name = "Pump!"
            desc = "Using the barbell to pump the chest and legs for a great pump."
            type = 2
        }
        for (workout in Workout.all()) {
            println("Created workout with id: ${workout.id}, name: ${workout.name}, desc: ${workout.desc}, type: ${workout.type}")
        }

        // Exercise List definitions (link table) assuming duration is in minutes
        val we_1 = WorkoutExercise.new {
            workout = w_1
            exercise = bw_ex_1
            order = 1
            sets = 3
            reps = 15
            duration = 10.00f
        }

        val we_2 = WorkoutExercise.new {
            workout = w_1
            exercise = bw_ex_2
            order = 2
            sets = 3
            reps = 10
            duration = 10.00f
        }

        val we_3 = WorkoutExercise.new {
            workout = w_2
            exercise = c_ex_2
            order = 1
            sets = 1
            reps = 1
            duration = 10.00f
        }

        val we_4 = WorkoutExercise.new {
            workout = w_2
            exercise = r_ex_1
            order = 2
            sets = 3
            reps = 6
            duration = 12.00f
        }

        val we_5 = WorkoutExercise.new {
            workout = w_2
            exercise = m_ex_2
            order = 3
            sets = 3
            reps = 3
            duration = 12.00f
        }

        val we_6 = WorkoutExercise.new {
            workout = w_3
            exercise = c_ex_1
            order = 1
            sets = 2
            reps = 1
            duration = 15.00f
        }

        val we_7 = WorkoutExercise.new {
            workout = w_4
            exercise = c_ex_2
            order = 1
            sets = 2
            reps = 1
            duration = 15.00f
        }

        val we_8 = WorkoutExercise.new {
            workout = w_5
            exercise = r_ex_1
            order = 1
            sets = 3
            reps = 6
            duration = 12.00f
        }

        val we_9 = WorkoutExercise.new {
            workout = w_5
            exercise = r_ex_2
            order = 2
            sets = 3
            reps = 6
            duration = 12.00f
        }
        for (we in WorkoutExercise.all()) {
            println("Created workout-exercise link with " +
                    "id: ${we.id}, " +
                    "workout_id: ${we.workout.id}, " +
                    "exercise_id: ${we.exercise.id}, " +
                    "order: ${we.order}, " +
                    "sets: ${we.sets}, " +
                    "reps: ${we.reps}, " +
                    "duration: ${we.duration} mins")
        }
    }

    configureRouting()
    configureTemplates()
    configureSessions()
}