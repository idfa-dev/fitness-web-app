package com.example

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import com.example.database.*

fun main(args: Array<String>) {
    EngineMain.main(args)

    Database.connect("jdbc:h2:mem:fitness", driver = "org.h2.Driver") // Starts com.example.database connection (fitness = dbname)

    transaction {
        SchemaUtils.create(Users)

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
}

fun Application.module() {
    configureRouting()
    configureTemplates()
}
