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

import org.h2.tools.Server // H2 web console
import org.jetbrains.exposed.v1.jdbc.exists

fun Application.module() {

    install(ContentNegotiation) {
        json()
    }

    Database.connect("jdbc:h2:file:./data/fitness", driver = "org.h2.Driver") // Starts com.example.database connection (fitness = dbname)

    // Starts web server connection to view database
    // To view database, simply go to 127.0.0.1:8082 and follow the instructions below
    //      1). Ensure Generic H2 (Embedded) is selected in settings
    //      2). Ensure Driver Class is set to org.h2.Driver
    //      3). Ensure JDBC URL is set to jdbc:h2:file:./data/fitness
    //      4). Do not enter a username or password (leave blank)
    //      5). Press 'Connect' to enter the database viewer

    Server.createWebServer(
        "-web",
        "-webPort",
        "8082",
        "-webAllowOthers"
    ).start()


    transaction {
        addLogger(StdOutSqlLogger)

        //resetDatabase() // Only uncomment for testing!

        SchemaUtils.create(Users, PTs, Exercises, Clients, Workouts, UserWorkouts, WorkoutExercises)

        if (User.all().empty()) {
            seedDummyValues()
        }
    }

    configureRouting()
    configureTemplates()
    configureSessions()
}