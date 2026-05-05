package com.example

import com.example.database.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.pebble.PebbleContent
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

//Authentication stuff
import com.example.database.authenticateUser //importing authentication from auth.kt
import com.example.database.authenticatePT
import com.example.database.doesCollide //importing collision checker from auth.kt
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters

import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction //to mod db
import io.ktor.server.request.receiveParameters
import io.ktor.server.sessions.*

fun Application.configureRouting() {
    routing {

        staticResources("/static", "static")

        get("/") {
            call.respond(PebbleContent("landing.peb", mapOf("currentPage" to "landing")))
        }

        get("/home") {
            val user = call.sessions.get<UserSession>()
            print(user?.id)
            print(user?.username)
            if (user != null) {
                call.respond(PebbleContent("home.peb", mapOf("currentPage" to "home")))
            }
            else {
                call.respond(PebbleContent("landing.peb", mapOf("currentPage" to "landing")))
            }
        }

        get("/current-workout") {
            val currentWorkoutSession = call.sessions.get<CurrentWorkoutSession>()
            print(currentWorkoutSession?.workoutSessionID)
            print(currentWorkoutSession?.userID)
            if (currentWorkoutSession == null) {
                call.respond(PebbleContent("start_workout.peb", mapOf("currentPage" to "current-workout")))
            }
            else {
                // Make sure this is fixed to contain workout info
                call.continueWorkout()
            }
        }

        post("/current-workout") {
            val parameters = call.receiveParameters()
            val useTemplate = parameters["use_template"]
            val exerciseChoice = parameters["exercise"]
            val removeExercise = parameters["remove_exercise"]
            val removeSet = parameters["remove_set"]
            val workoutSessionExerciseID = parameters["wseid"]?.toIntOrNull()
            val reps = parameters["reps"]?.toIntOrNull()
            val weight = parameters["weight"]?.toFloatOrNull()
            if (useTemplate == null) {
                if (exerciseChoice == null) {
                    if (removeExercise == null && removeSet == null) {
                        if (workoutSessionExerciseID == null || reps == null || weight == null) {
                            call.respond(HttpStatusCode.BadRequest)
                        }
                        else {
                            call.addSet(workoutSessionExerciseID,reps,weight)
                        }
                    }
                    else {
                        if (removeSet != null) {
                            // removeSet contains id of set to be removed
                            val wssid = removeSet.toIntOrNull()
                            if (wssid != null) {
                                call.removeSet(wssid)
                            }
                            else {
                                println("wssid is null")
                            }
                        }
                        else if (removeExercise != null) {
                            //call.removeExercise()
                        }
                    }
                }
                else {
                    // Handle adding an exercise
                    call.addExercise(exerciseChoice)
                }
            }
            else {
                if (useTemplate == "yes") {
                    // User is redirected to a page where a template can be selected (currently not implemented)
                    call.respond(PebbleContent("current_workout.peb", mapOf("currentPage" to "current-workout")))
                }
                else if (useTemplate == "no") {
                    // User is redirected to the main current-workout page, and a workout can be started
                    call.startNewWorkout()
                }
            }
        }

        get("/workouts") {
            call.respond(PebbleContent("workouts.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                call.displayWorkout(id)
            }
        }

        get("/workouts/view") {call.displayWorkouts()}

        get("/workouts/view/search") {call.searchWorkouts()}

        get("/workouts/create") {
            call.respond(PebbleContent("create_workout.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/create/cardio") {
            call.respond(PebbleContent("create_cardio_workout.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/create/bodyweight") {
            call.respond(PebbleContent("create_bodyweight_workout.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/create/resistance") {
            call.respond(PebbleContent("create_resistance_workout.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/create/mixed") {
            call.respond(PebbleContent("create_mixed_workout.peb", mapOf("currentPage" to "workouts")))
        }

        get("/exercises") {call.exercises()}

        get("/exercises/search") {call.searchExercises()}

        get("/exercises/{id}") {call.exercise()}

        get("/calendar") {
            val now = LocalDate.now()
            val month = now.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()
            val year = now.year
            val firstDay = now.withDayOfMonth(1)
            val lastDay = now.withDayOfMonth(now.lengthOfMonth())
            val startWeekday = (firstDay.dayOfWeek.value + 6) % 7 // Monday first

            val days = mutableListOf<String>()
            repeat(startWeekday) { days.add("") }
            for (i in 1..lastDay.dayOfMonth) days.add(i.toString())

            val remainder = days.size % 7
            if (remainder != 0) repeat(7 - remainder) { days.add("") }

            val weeks = days.chunked(7)

            val calendarTable = buildString {
                append("<div class='month-year'>$month $year</div>")
                append("<table class='calendar'>")
                append("<tr>")
                listOf("MON","TUE","WED","THU","FRI","SAT","SUN").forEach { append("<th>$it</th>") }
                append("</tr>")
                weeks.forEach { week ->
                    append("<tr>")
                    week.forEach { day ->
                        if(day.isEmpty()) append("<td></td>")
                        else append("<td><span class='day-number'>$day</span></td>")
                    }
                    append("</tr>")
                }
                append("</table>")
            }

            call.respond(PebbleContent("calendar.peb", mapOf(
                "currentPage" to "calendar",
                "calendarTable" to calendarTable
            )))
        }


        get("/profile") {
            call.respond(PebbleContent("profile.peb", mapOf("currentPage" to "profile")))
        }

        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/sign-in")
        }

        get("/profile/profile_info") {
            val session = call.sessions.get<UserSession>()

            if (session == null) {
                call.respondRedirect("/sign-in")
                return@get
            }

            val user = transaction {
                session.id.toIntOrNull()?.let { User.findById(it) }
            }

            val context = mutableMapOf<String, Any>(
                "currentPage" to "profile"
            )

            user?.let { context["user"] = it }
            call.respond(PebbleContent("profile_info.peb", context))
        }

        post("/profile/profile_info") {
            val params = call.receiveParameters()

            val fname = params["fname"]
            val sex = params["sex"]
            val age = params["age"]
            val height = params["height"]?.toFloatOrNull()
            val weight = params["weight"]?.toFloatOrNull()

            println("POST ROUTE HIT")

            val session = call.sessions.get<UserSession>()

            if (session == null) {
                call.respondRedirect("/sign-in")
                return@post
            }

            transaction {
                val user = session.id.toIntOrNull()?.let { User.findById(it) }

                if (user != null) {
                    if (!fname.isNullOrBlank()) user.fname = fname
                    if (!sex.isNullOrBlank()) user.sex = sex
                    height?.let { user.height = it }
                    weight?.let { user.weight = it }
                    if (!age.isNullOrBlank()) {
                        user.age = age.toInt()
                    }
                }
            }
            call.respondRedirect("/profile/profile_info")
        }

        get("/sign-in") {
            call.respond(PebbleContent("sign-in.peb", mapOf("currentPage" to "sign-in")))
        }

        post("/sign-in") { //This whole section will be added to Auth.kt eventually or like modularized

            
            val parameters = call.receiveParameters() //pull website input
            var isPT = false

            val username = parameters["username"] ?: "Input not received."  // Second case for input check
            val password = parameters["password"] ?: "Input not received."

            println("Username: $username, Password: $password") // Input check for sign-in
            
            var isValid = authenticateUser(username, password) //check
            if (!isValid)
            {
                isValid = authenticatePT(username, password) //Double check if user is a PT
                isPT = true
            }
            if (isValid) {
                transaction {
                    val userID: Int? = if (isPT) {
                        getPTIdByUsername(username)
                    } else {
                        getUserIdByUsername(username)
                    }
                    call.sessions.set(UserSession(id=userID.toString(), username=username))
                    call.sessions.clear<CurrentWorkoutSession>()
                }
                call.respondRedirect("/home")   // Redirects ot the actual home page
            }
            else 
            {
                call.respondText("Invalid details. Please try again.")  // Error message on failure
            }

        }

         get("/sign-up") {
            call.respond(PebbleContent("sign-up.peb", mapOf("currentPage" to "sign-up")))
        }

        post("/sign-up") {  //This whole section will be added to User/Users eventually.
            
            val parameters = call.receiveParameters()

            
            val _username = parameters["username"] ?: "Input not received."
            val _email = parameters["email"] ?: "Input not received."
            val _password = parameters["password"] ?: "Input not received."
            val _usertype = parameters["usertype"] ?: "Input not received."
            
            //Prints the actual like user input for the log in
            println("Username: $_username, Email: $_email, Password: $_password") // debug


            //collision check
            val userExists = doesCollide(_username, _email)

            var success = false

            if ( userExists ) {
                call.respondText("User already exists. Please sign in instead.")
                return@post 
            }
            else
            {
                if  (_usertype == "3")
                {
                    transaction {
                        PT.new {
                            username = _username
                            password = _password
                            email = _email 
                            fname = ""
                            height = 0f
                            weight = 0f
                            dob = ""
                            sex = ""
                        }
                    }
                }
                else
                {
                    transaction {
                        User.new {
                            type = 0              // Would love to add type but currenty not sure
                            username = _username   // how that works iwth this number system, 
                            password = _password   // have to discuss it first
                            email = _email
                            fname = ""
                            height = 0f
                            weight = 0f
                            dob = ""
                            sex = ""
                        }
                    }
                }

                success = true
            }

            if (success) {
                call.respondRedirect("/sign-in") // redirect to sign-in
            } else {    //error
                call.respondText("Failed to create user. Please try again.")
            }
        }
    }
}