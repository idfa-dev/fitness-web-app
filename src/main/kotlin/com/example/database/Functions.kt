package com.example.database

fun getUserIdByUsername(username: String): Int {
    return User.all().single { it.username == username }.id.value // The reason we put .value after id is because .id returns EntityID<Int> (it is wrapped)
}