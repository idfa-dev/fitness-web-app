package com.example.database

import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.selectAll

fun authenticateUser(username: String, password: String): Boolean {
    return transaction {
        Users.selectAll().where {
            (Users.username eq username) and (Users.password eq password)
        }.firstOrNull() != null
    }
}