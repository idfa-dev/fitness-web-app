package com.example

//DB
import com.example.database.*

//Server
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.pebble.PebbleContent
import io.ktor.server.request.receiveParameters
import io.ktor.server.sessions.*

//jav
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

//Authentication imports
import com.example.database.User
import com.example.database.PT
//Authentication stuff
import com.example.database.authenticateUser //importing authentication from auth.kt
import com.example.database.authenticatePT
import com.example.database.doesCollide //importing collision checker from auth.kt
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters

//SQL ( might be unnecessary )
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction //to mod db
<<<<<<< HEAD
import org.jetbrains.exposed.v1.jdbc.insert
import io.ktor.server.request.receiveParameters
import io.ktor.server.sessions.*
=======

>>>>>>> dba9031c9d50ad6b7b3787e107d4149935f82cc8

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

            val userId = call.sessions.get<UserSession>()?.id?.toIntOrNull()

            val now = LocalDate.now()

            val monthParam = call.request.queryParameters["month"]?.toIntOrNull()
            val yearParam = call.request.queryParameters["year"]?.toIntOrNull()

            val safeMonth = monthParam?.coerceIn(1, 12)
            val safeYear = yearParam ?: now.year

            val date = if (safeMonth != null) {
                LocalDate.of(safeYear, safeMonth, 1)
            } else {
                now
            }

            val competitions = transaction {
                if (userId == null) return@transaction emptyList()

                Competition.find { Competitions.user eq userId }
                    .map { c ->
                        mapOf(
                            "id" to c.id.value,
                            "name" to c.name,
                            "date" to c.date.toString(),
                            "time" to c.time,
                            "distance" to c.distance,
                            "finishTime" to c.finishTime,
                            "types" to (c.types?.split(",") ?: emptyList())
                        )
                    }
            }



            val month = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()
            val year = date.year
            val monthNumber = date.monthValue

            val firstDay = date.withDayOfMonth(1)
            val lastDay = date.withDayOfMonth(date.lengthOfMonth())
            val startWeekday = (firstDay.dayOfWeek.value + 6) % 7

            val days = mutableListOf<String>()
            repeat(startWeekday) { days.add("") }
            for (i in 1..lastDay.dayOfMonth) days.add(i.toString())

            val remainder = days.size % 7
            if (remainder != 0) repeat(7 - remainder) { days.add("") }

            val weeks = days.chunked(7)

            val (prevMonth, prevYear) =
                if (date.monthValue == 1) 12 to date.year - 1 else date.monthValue - 1 to date.year

            val (nextMonth, nextYear) =
                if (date.monthValue == 12) 1 to date.year + 1 else date.monthValue + 1 to date.year

            val calendarTable = buildString {
                append("<table class='calendar'>")
                append("<tr>")
                listOf("MON","TUE","WED","THU","FRI","SAT","SUN").forEach { append("<th>$it</th>") }
                append("</tr>")
                weeks.forEach { week ->
                    append("<tr>")
                    week.forEach { day ->
                        if(day.isEmpty()) {
                            append("<td></td>")
                        }
                        else {
                            append("<td><span class='day-number'>$day</span>")

                            competitions.forEach { c: Map<String, Any?> ->

                                val cDate = java.time.LocalDate.parse(c["date"].toString())
                                val cellDate = java.time.LocalDate.of(year, monthNumber, day.toInt())

                                if (cDate == cellDate) {

                                    val infoJson = """
                                    {
                                    "id": "${c["id"]}",
                                    "name": "${c["name"]}",
                                    "date": "${c["date"]}",
                                    "time": "${c["time"] ?: ""}",
                                    "distance": "${c["distance"] ?: ""}",
                                    "finishTime": "${c["finishTime"] ?: ""}",
                                    "types": ${ (c["types"]?.toString()?.split(",") ?: emptyList())
                                        .joinToString(prefix = "[", postfix = "]") { "\"$it\"" } }
                                    }
                                    """.trimIndent()
                                        .replace("\n", "")
                                        .replace("\"", "&quot;")

                                    append("<div class='comp-pill' data-info=\"$infoJson\">${c["name"]}</div>")
                                }
                            }

                            append("</td>")
                        }
                    }
                    append("</tr>")
                }
                append("</table>")
            }

            call.respond(PebbleContent("calendar.peb", mapOf(
                "currentPage" to "calendar",
                "calendarTable" to calendarTable,
                "month" to month,
                "year" to year,
                "prevMonth" to prevMonth,
                "prevYear" to prevYear,
                "nextMonth" to nextMonth,
                "nextYear" to nextYear,
                "monthNumber" to monthNumber,
                "competitions" to competitions,
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
            
            signInHandler(call)
            
        }

        get("/sign-up") {
            call.respond(PebbleContent("sign-up.peb", mapOf("currentPage" to "sign-up")))
        }

        post("/sign-up") {  //This whole section will be added to User/Users eventually.
            signUpHandler(call)
        }

        post("/competitions/add") {
            val params = call.receiveParameters()

            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            val uid = session.id.toIntOrNull()
            if (uid == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val competitionName = params["name"]
            val competitionDate = params["date"]
            val competitionTime = params["time"]
            val competitionDistance = params["distance"]?.toDoubleOrNull()
            val competitionFinishTime = params["finishTime"]

            val competitionTypes = params["types"]
                ?.split(",")
                ?.map { it.trim() }
                ?: emptyList()

            if (competitionName.isNullOrBlank() || competitionDate.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val compId = params["id"]?.toIntOrNull()

            transaction {
                if (compId != null) {
                    val comp = Competition.findById(compId)

                    if (comp != null) {
                        comp.name = competitionName
                        comp.date = java.time.LocalDate.parse(competitionDate)
                        comp.time = competitionTime
                        comp.distance = competitionDistance
                        comp.finishTime = competitionFinishTime
                        comp.types = competitionTypes.joinToString(",")
                    }
                } else {
                    Competitions.insert {
                        it[Competitions.user] = uid
                        it[Competitions.name] = competitionName
                        it[Competitions.date] = java.time.LocalDate.parse(competitionDate)
                        it[Competitions.time] = competitionTime
                        it[Competitions.distance] = competitionDistance
                        it[Competitions.finishTime] = competitionFinishTime
                        it[Competitions.types] = competitionTypes.joinToString(",")
                    }
                }
            }

            call.respondRedirect("/calendar")
        }

        post("/competitions/delete") {
            val params = call.receiveParameters()
            val compId = params["id"]?.toIntOrNull()

            if (compId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            transaction {
                val comp = Competition.findById(compId)
                comp?.delete()
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}