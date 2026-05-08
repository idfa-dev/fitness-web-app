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

import org.jetbrains.exposed.v1.core.*

import org.jetbrains.exposed.v1.jdbc.transactions.transaction // To modify db
import org.jetbrains.exposed.v1.jdbc.insert
import io.ktor.server.request.receiveParameters
import io.ktor.server.sessions.*
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import java.time.Instant

//JSON
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

fun Application.configureRouting() {
    routing {

        staticResources("/static", "static")


        // Test Routes
        post("/test/sign-in") {
            val parameters = call.receiveParameters()
            val userID = parameters["userID"]
            val username = parameters["username"]
            if (userID != null && username != null) {
                call.sessions.set(UserSession(userID, username))
                println("UserSession set")
                call.respond(HttpStatusCode.OK)
            }
        }

        get("/test/set-all-workouts-as-not-favourited") {
            suspendTransaction {
                val workouts = Workout.all().toList()
                if (workouts.isNotEmpty()) {
                    for (workout in workouts) {
                        Workout.findByIdAndUpdate(workout.id.value) {
                            it.favourite = false
                        }
                    }
                    println("All workouts set to favourited=false")
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        post("/test/change-favourite-workouts") {
            val parameters = call.receiveParameters()
            suspendTransaction {
                val workoutName = parameters["workoutName"]
                // Change favourite to true for given workout
                val workoutID = Workout.all().firstOrNull {it.name == workoutName}?.id?.value
                if (workoutID != null) {
                    Workout.findByIdAndUpdate(workoutID) {
                        it.favourite = !it.favourite
                    }
                    println("Switched the favourite state of workout with name = $workoutName")
                    call.respond(HttpStatusCode.OK)
                }
                else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        post("/test/create-workout-session") {
            // Create workout session for use in testing
            val parameters = call.receiveParameters()
            val userID = parameters["userID"]
            val username = parameters["username"]
            suspendTransaction {
                val userEntity = User.all().first {it.id.value == userID?.toInt()}
                if (userID != null && username != null) {
                    val ws = WorkoutSession.new { user = userEntity }
                    call.sessions.set(CurrentWorkoutSession(ws.id.value.toString(), userID))
                    println("CurrentWorkoutSession set")
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        // Regular Routes

        get("/") {
            call.respond(PebbleContent("landing/auth/landing.peb", mapOf("currentPage" to "landing")))
        }

        get("/home") {
            val user = call.sessions.get<UserSession>()
            print(user?.id)
            print(user?.username)
            if (user != null) {
                call.respond(PebbleContent("home/home.peb", mapOf("currentPage" to "home")))
            }
            else {
                call.respond(PebbleContent("landing/auth/landing.peb", mapOf("currentPage" to "landing")))
            }
        }

        get("/current-workout") {
            val currentWorkoutSession = call.sessions.get<CurrentWorkoutSession>()
            print(currentWorkoutSession?.workoutSessionID)
            print(currentWorkoutSession?.userID)
            if (currentWorkoutSession == null) {
                call.respond(PebbleContent("current_workout/start_workout.peb", mapOf("currentPage" to "current-workout")))
            }
            else {
                call.continueWorkout()
            }
        }

        post("/current-workout") {
            val parameters = call.receiveParameters()

            when {
                parameters["workout_template"] != null -> {
                    val workoutID = parameters["workout_template"]?.toIntOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    // User has a workout created with the workout selected
                    call.startNewTemplateWorkout(workoutID)

                }

                parameters["use_template"] == "yes" -> {
                    // User is redirected to a page where a template can be selected
                    call.selectWorkoutTemplate()
                }

                parameters["use_template"] == "no" -> {
                    // User is redirected to the main current-workout page, and a workout can be started
                    call.startNewWorkout()
                }

                parameters["exercise"] != null -> {
                    // Handle adding an exercise
                    call.addExercise(parameters["exercise"]!!)
                }

                parameters["remove_set"] != null -> {
                    // removeSet contains id of set to be removed
                    val wssid = parameters["remove_set"]?.toIntOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    call.removeSet(wssid)
                }

                parameters["remove_exercise"] != null -> {
                    // removeExercise contains id of exercise to be removed
                    val wseid = parameters["remove_exercise"]?.toIntOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    call.removeExercise(wseid)
                }

                parameters["wseid"] != null && parameters["reps"] != null && parameters["weight"] != null -> {
                    val wseid = parameters["wseid"]?.toIntOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val reps = parameters["reps"]?.toIntOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val weight = parameters["weight"]?.toFloatOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    call.addSet(wseid, reps, weight)
                }

                parameters["end"] == "1" -> {
                    // User workout session is ended
                    call.endWorkout()
                }

                else -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }
        }

        get("/workouts") {
            call.respond(PebbleContent("workouts/workouts.peb", mapOf("currentPage" to "workouts")))
        }

        get("/workouts/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                call.displayWorkout(id)
            }
        }

        get("/workouts/view") {call.displayWorkouts()}

        get("/workouts/view/search") {call.searchWorkouts()}

        post("/workouts/view") {
            val parameters = call.receiveParameters()
            when {
                parameters["favourite"] != null -> {
                    val favourite = parameters["favourite"]?.toIntOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val from = call.request.queryParameters["from"] ?: "workouts"
                    call.displayWorkouts(favourite, from)
                }
            }
        }

        get("/workouts/past") { call.displayPastWorkouts() }

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

            val exercises = transaction {
                if (userId == null) return@transaction emptyList()

                CalendarExercise.find { CalendarExercises.user eq userId }
                    .map {
                        mapOf(
                            "id" to it.id.value,
                            "name" to it.name,
                            "date" to it.date.toString(),
                            "timeTaken" to it.timeTaken,
                            "calories" to it.calories,
                            "distance" to it.distance,
                            "machine" to it.machine,
                            "weight" to it.weight,
                            "setsReps" to it.setsReps,
                            "type" to it.type
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

                            val exercisesForDay = exercises.filter {
                                java.time.LocalDate.parse(it["date"].toString()) ==
                                        java.time.LocalDate.of(year, monthNumber, day.toInt())
                            }

                            val groupedExercises = exercisesForDay.groupBy {
                                it["type"]?.toString() ?: "Mixed"
                            }

                            groupedExercises.forEach { (type, exList) ->

                                val colourClass = when(type) {
                                    "Cardio" -> "red"
                                    "Body Weight" -> "blue"
                                    "Resistance" -> "green"
                                    else -> "yellow"
                                }

                                val exJson = exList.joinToString(
                                    prefix = "[",
                                    postfix = "]"
                                ) { ex ->

                                    """
                                    {
                                        "id":"${ex["id"]}",
                                        "name":"${ex["name"]}",
                                        "date":"${ex["date"]}",
                                        "distance":"${ex["distance"] ?: ""}",
                                        "timeTaken":"${ex["timeTaken"] ?: ""}",
                                        "weight":"${ex["weight"] ?: ""}",
                                        "machine":"${ex["machine"] ?: ""}",
                                        "calories":"${ex["calories"] ?: ""}",
                                        "setsReps":"${ex["setsReps"] ?: ""}",
                                        "type":"${ex["type"] ?: ""}"
                                    }
                                    """.trimIndent()
                                }
                                    .replace("\n", "")
                                    .replace("\"", "&quot;")

                                append("""
                                    <div class='exercise-dot $colourClass'
                                        data-exercises="$exJson">
                                    </div>
                                """.trimIndent())
                            }

                            append("</td>")
                        }
                    }
                    append("</tr>")
                }
                append("</table>")
            }

            call.respond(PebbleContent("calendar/calendar.peb", mapOf(
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
                "exercises" to exercises,
            )))
        }


        get("/profile") {

            val userSession = call.sessions.get<UserSession>()
                ?: return@get call.respondRedirect("/sign-in")

            val sessions = transaction {
                WorkoutSession.all()
                    .filter {
                        it.user.id.toString() == userSession.id &&
                        it.complete &&
                        it.endedAt != null
                    }
                    .toList()
            }

            println("PROFILE SESSIONS FOUND = ${sessions.size}")

            sessions.forEach {
                println("SESSION COMPLETE = ${it.complete}")
                println("START = ${it.startedAt}")
                println("END = ${it.endedAt}")
            }

            val weeklyData = calculateWeeklyWorkoutMinutes(sessions)

            println("WEEKLY DATA = $weeklyData")

            val weeklyDataJson = Json.encodeToString(weeklyData)

            call.respond(
                PebbleContent(
                    "profile/profile.peb",
                    mapOf(
                        "currentPage" to "profile",
                        "weeklyData" to weeklyDataJson
                    )
                )
            )
        }

        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("landing/auth/sign-in")
        }

        get("/profile/profile_info") {
            val session = call.sessions.get<UserSession>()

            if (session == null) {
                call.respondRedirect("landing/auth/sign-in")
                return@get
            }

            val user = transaction {
                session.id.toIntOrNull()?.let { User.findById(it) }
            }

            val context = mutableMapOf<String, Any>(
                "currentPage" to "profile"
            )

            user?.let { context["user"] = it }
            call.respond(PebbleContent("profile/profile_info.peb", context))
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
            call.respond(PebbleContent("landing/auth/sign-in.peb", mapOf("currentPage" to "sign-in")))
        }

        post("/sign-in") { //This whole section will be added to Auth.kt eventually or like modularized
            signInHandler(call)
        }

        get("/sign-up") {
            call.respond(PebbleContent("landing/auth/sign-up.peb", mapOf("currentPage" to "sign-up")))
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

        post("/calendar-exercises/add") {
            val params = call.receiveParameters()

            val session = call.sessions.get<UserSession>()
                ?: return@post call.respond(HttpStatusCode.Unauthorized)

            val uid = session.id.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            val id = params["id"]?.toIntOrNull()

            val name = params["name"]
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            val date = params["date"]
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            transaction {
                if (id != null) {
                    val ex = CalendarExercise.findById(id)
                    if (ex != null) {
                        ex.name = name
                        ex.date = java.time.LocalDate.parse(date)
                        ex.timeTaken = params["timeTaken"]
                        ex.calories = params["calories"]?.toIntOrNull()
                        ex.distance = params["distance"]?.toDoubleOrNull()
                        ex.machine = params["machine"]
                        ex.weight = params["weight"]?.toDoubleOrNull()
                        ex.setsReps = params["setsReps"]
                        ex.type = params["type"]
                    }
                } else {
                    CalendarExercises.insert {
                        it[CalendarExercises.user] = uid
                        it[CalendarExercises.name] = name
                        it[CalendarExercises.date] = LocalDate.parse(date)

                        it[CalendarExercises.timeTaken] = params["timeTaken"]
                        it[CalendarExercises.calories] = params["calories"]?.toIntOrNull()
                        it[CalendarExercises.distance] = params["distance"]?.toDoubleOrNull()
                        it[CalendarExercises.machine] = params["machine"]
                        it[CalendarExercises.weight] = params["weight"]?.toDoubleOrNull()
                        it[CalendarExercises.setsReps] = params["setsReps"]
                        it[CalendarExercises.type] = params["type"]
                    }
                }
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/calendar-exercises/delete") {
            val params = call.receiveParameters()
            val id = params["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            transaction {
                CalendarExercise.findById(id)?.delete()
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}