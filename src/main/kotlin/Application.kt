package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)

    Database.connect("jdbc:h2:mem:fitness", driver = "org.h2.Driver") // Starts database connection (fitness = dbname)
}

fun Application.module() {
    configureRouting()
    configureTemplates()
}
