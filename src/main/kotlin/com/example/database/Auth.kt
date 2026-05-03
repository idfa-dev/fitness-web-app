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

fun authenticatePT(username: String, password: String): Boolean {
    return transaction {
        PTs.selectAll().where {
            (PTs.username eq username) and (PTs.password eq password)
        }.firstOrNull() != null
    }
}

fun doesCollide(username: String, email: String): Boolean {
    return transaction {
        Users.selectAll().where {
            (Users.username eq username) or (Users.email eq email)
        }.firstOrNull() != null
    }
}