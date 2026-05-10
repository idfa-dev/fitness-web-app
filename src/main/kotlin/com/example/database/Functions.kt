package com.example.database

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.WorkoutSessionExerciseObject
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.time.LocalDate

fun seedDummyValues() {
    transaction {
        println("Seeding users...")

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

        println("Seeding workouts...")

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

        println("Seeding workoutExercises...")

        // WorkoutExercise definitions (link table) assuming duration is in minutes
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
            println(
                "Created workout-exercise link with " +
                        "id: ${we.id}, " +
                        "workout_id: ${we.workout.id}, " +
                        "exercise_id: ${we.exercise.id}, " +
                        "order: ${we.order}, " +
                        "sets: ${we.sets}, " +
                        "reps: ${we.reps}, " +
                        "duration: ${we.duration} mins"
            )
        }

        println("Seeding demo users....")

        var pw1 = "bartyBoy123A!"
        pw1 = BCrypt.withDefaults().hashToString(8, pw1.toCharArray())
        var pw2 = "vinCent4life£"
        pw2 = BCrypt.withDefaults().hashToString(8, pw2.toCharArray())
        val experiencedGymgoer = User.create(1, "barto123", pw1, "bartholomew123@gmail.com")
        val compUser = User.create(2, "vincyWincySpider", pw2, "vincentComp@gmail.com")

        println("Created Experienced Gymgoer with id = ${experiencedGymgoer.id} and username = ${experiencedGymgoer.username}")
        println("Created Competitive User with id = ${compUser.id} and username = ${compUser.username}")

        println("Seeding demo user data...\n")

        // Bartholomew (Experienced Gymgoer)

        // Needs past workout sessions, exercises and sets

        val exercises = Exercise.all().toList()


        // Workout sessions

        val ws1 = WorkoutSession.new {
            user = experiencedGymgoer
            startedAt = Instant.parse("2026-04-27T09:15:00Z")
            endedAt = Instant.parse("2026-04-27T10:10:30Z")
            complete = true
        }

        val ws2 = WorkoutSession.new {
            user = experiencedGymgoer
            startedAt = Instant.parse("2026-04-30T17:45:00Z")
            endedAt = Instant.parse("2026-04-30T18:50:15Z")
            complete = true
        }

        val ws3 = WorkoutSession.new {
            user = experiencedGymgoer
            startedAt = Instant.parse("2026-05-02T11:00:00Z")
            endedAt = Instant.parse("2026-05-02T12:05:20Z")
            complete = true
        }

        val ws4 = WorkoutSession.new {
            user = experiencedGymgoer
            startedAt = Instant.parse("2026-05-04T07:30:00Z")
            endedAt = Instant.parse("2026-05-04T08:35:00Z")
            complete = true
        }

        val ws5 = WorkoutSession.new {
            user = experiencedGymgoer
            startedAt = Instant.parse("2026-05-06T18:10:00Z")
            endedAt = Instant.parse("2026-05-06T19:15:35Z")
            complete = true
        }

        val ws6 = WorkoutSession.new {
            user = experiencedGymgoer
            startedAt = Instant.parse("2026-05-09T16:10:00Z")
            endedAt = Instant.parse("2026-05-09T17:05:40Z")
            complete = true
        }

        // Workout session exercises

        val wse1 = WorkoutSessionExercise.new {
            workoutSession = ws1
            exercise = exercises[2]
            order = 1
        }

        val wse2 = WorkoutSessionExercise.new {
            workoutSession = ws1
            exercise = exercises[4]
            order = 2
        }

        val wse3 = WorkoutSessionExercise.new {
            workoutSession = ws2
            exercise = exercises[3]
            order = 1
        }

        val wse4 = WorkoutSessionExercise.new {
            workoutSession = ws2
            exercise = exercises[5]
            order = 2
        }

        val wse5 = WorkoutSessionExercise.new {
            workoutSession = ws2
            exercise = exercises[6]
            order = 3
        }

        val wse6 = WorkoutSessionExercise.new {
            workoutSession = ws3
            exercise = exercises[7]
            order = 1
        }

        val wse7 = WorkoutSessionExercise.new {
            workoutSession = ws3
            exercise = exercises[4]
            order = 2
        }

        val wse8 = WorkoutSessionExercise.new {
            workoutSession = ws4
            exercise = exercises[7]
            order = 1
        }

        val wse9 = WorkoutSessionExercise.new {
            workoutSession = ws4
            exercise = exercises[4]
            order = 2
        }

        val wse10 = WorkoutSessionExercise.new {
            workoutSession = ws5
            exercise = exercises[3]
            order = 1
        }

        val wse11 = WorkoutSessionExercise.new {
            workoutSession = ws5
            exercise = exercises[6]
            order = 2
        }

        val wse12 = WorkoutSessionExercise.new {
            workoutSession = ws6
            exercise = exercises[3]
            order = 1
        }

        // Workout session sets

        WorkoutSessionSet.new {
            workoutSessionExercise = wse1
            reps = 15
            weight = 0.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse1
            reps = 12
            weight = 0.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse2
            reps = 8
            weight = 90.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse2
            reps = 7
            weight = 90.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse2
            reps = 6
            weight = 80.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse3
            reps = 10
            weight = 60.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse3
            reps = 9
            weight = 60.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse4
            reps = 12
            weight = 40.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse4
            reps = 10
            weight = 40.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse4
            reps = 8
            weight = 35.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse5
            reps = 6
            weight = 110.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse5
            reps = 5
            weight = 110.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse6
            reps = 5
            weight = 120.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse6
            reps = 5
            weight = 120.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse6
            reps = 4
            weight = 115.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse7
            reps = 8
            weight = 85.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse7
            reps = 7
            weight = 85.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse8
            reps = 6
            weight = 120.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse8
            reps = 5
            weight = 120.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse8
            reps = 4
            weight = 115.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse9
            reps = 8
            weight = 85.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse9
            reps = 7
            weight = 85.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse10
            reps = 12
            weight = 50.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse10
            reps = 10
            weight = 50.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse10
            reps = 8
            weight = 45.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse11
            reps = 6
            weight = 100.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse11
            reps = 5
            weight = 100.0f
        }

        WorkoutSessionSet.new {
            workoutSessionExercise = wse12
            reps = 12
            weight = 50.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse12
            reps = 10
            weight = 50.0f
        }
        WorkoutSessionSet.new {
            workoutSessionExercise = wse12
            reps = 8
            weight = 45.0f
        }

        // Vincent (Competitive User)

        // Needs calendar exercises and competitions

        // Calendar exercises


        val ce1 = CalendarExercise.new {
            user = compUser
            name = "Morning Run"
            date = LocalDate.of(2026, 5, 9)
            timeTaken = "22:45"
            calories = 230
            distance = 3.7
            machine = "Treadmill"
            type = "Cardio"
        }

        val ce2 = CalendarExercise.new {
            user = compUser
            name = "Upper Body Strength"
            date = LocalDate.of(2026, 5, 7)
            calories = 300
            weight = 75.0
            setsReps = "4x8"
            type = "Resistance"
        }

        val ce3 = CalendarExercise.new {
            user = compUser
            name = "HIIT Circuit"
            date = LocalDate.of(2026, 5, 5)
            timeTaken = "25:00"
            calories = 350
            type = "Mixed"
        }

        val ce4 = CalendarExercise.new {
            user = compUser
            name = "Bodyweight Core Session"
            date = LocalDate.of(2026, 5, 3)
            timeTaken = "20:00"
            calories = 160
            setsReps = "3x20"
            type = "Bodyweight"
        }

        val ce5 = CalendarExercise.new {
            user = compUser
            name = "Cycling Intervals"
            date = LocalDate.of(2026, 4, 30)
            timeTaken = "45:00"
            calories = 450
            distance = 18.6
            machine = "Exercise Bike"
            type = "Cardio"
        }

        val ce6 = CalendarExercise.new {
            user = compUser
            name = "Lower Body Strength"
            date = LocalDate.of(2026, 4, 28)
            calories = 300
            weight = 95.0
            setsReps = "5x5"
            type = "Resistance"
        }


        val comp1 = Competition.new {
            user = compUser
            name = "Leeds Half Marathon"
            date = LocalDate.of(2026, 5, 10)
            time = "09:00"
            distance = 21.1
            finishTime = "1:42:18"
            types = "Running"
        }

        val comp2 = Competition.new {
            user = compUser
            name = "Top of the Rock Gravel-X"
            date = LocalDate.of(2026, 6, 20)
            time = "12:30"
            types = "Cycling"
        }

        val comp3 = Competition.new {
            user = compUser
            name = "Indoor Swim Challenge"
            date = LocalDate.of(2026, 3, 22)
            time = "10:00"
            distance = 1.5
            finishTime = "29:10"
            types = "Swimming"
        }

        val comp4 = Competition.new {
            user = compUser
            name = "Winter 10K Road Race"
            date = LocalDate.of(2026, 2, 16)
            time = "09:15"
            distance = 10.0
            finishTime = "44:05"
            types = "Running"
        }

        println("\nSUCCESSFULLY SEEDED DATA\n")
    }
}

fun resetDatabase() {
    transaction {
        exec("DROP ALL OBJECTS")
        SchemaUtils.create(
            Users, PTs, Exercises,
            Clients, Workouts, WorkoutExercises,
            WorkoutSessions, WorkoutSessionExercises,
            WorkoutSessionSets, Competitions, CalendarExercises
        )
    }
    println("\nSUCCESSFULLY RESET DATABASE\n")
}

fun getUserIdByUsername(username: String): Int {
    return User.all().single { it.username == username }.id.value // The reason we put .value after id is because .id returns EntityID<Int> (it is wrapped)
}

fun getPTIdByUsername(username: String): Int {
    return PT.all().single { it.username == username }.id.value // The reason we put .value after id is because .id returns EntityID<Int> (it is wrapped)
}

fun getListOfWorkoutSessionExercises(ws: WorkoutSession): List<WorkoutSessionExerciseObject> {
    val list = mutableListOf<WorkoutSessionExerciseObject>()
    // Search for all WorkoutSessionExercise with workoutSession == workoutSession
    val workoutSessionExercises = WorkoutSessionExercise.all().filter {it.workoutSession == ws}.toList()
    for (wse in workoutSessionExercises) {
        // Search for all sets currently in the database attached to this exercise, then add as an object
        val wss = WorkoutSessionSet.all().filter {it.workoutSessionExercise == wse}.toList()
        val wseo = WorkoutSessionExerciseObject (
            wsExercise = wse,
            sets = wss
        )
        list.add(wseo) // Even if wss is empty, it adds the WorkoutSessionExerciseObject with sets = empty list which is ideal
    }
    return list
}