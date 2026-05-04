package com.example.database

//SQL
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.selectAll

//Hashing
import at.favre.lib.crypto.bcrypt.BCrypt

//Ktor Server stuff
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

import com.example.UserSession

//Simple redirect, if true, redirect, else show an error
suspend fun redirectHandler( call: ApplicationCall, success: Boolean, redirect: String, errormsg: String){ 
    if (success) {
        call.respondRedirect(redirect)
    } else {
        call.respondText(errormsg)
    }
}

suspend fun signInHandler( call: ApplicationCall ) {

    val parameters = call.receiveParameters() //pull website input
    var isPT = false

    val username = parameters["username"] ?: "Input not received."  // Second case for input check
    val password = parameters["password"] ?: "Input not received."
    
    println("Username: $username, Password: $password") // Input check for sign-in
    
    var isValid = authenticateUser(username, password) //check

    if (!isValid)
    {
        isValid = authenticatePT(username, password) //Double check if user is a PT
        isPT = isValid //fixed, used to be 'isPT = true'
    }
    if (isValid) {
        transaction {
            val userID: Int? = if (isPT) {
                getPTIdByUsername(username)
            } else {
                getUserIdByUsername(username)
            }
            call.sessions.set(UserSession(id=userID.toString(), username=username))
        }
        
    }

    redirectHandler( call, isValid, "/home", "Invalid details." )
    //
}

suspend fun signUpHandler(call: ApplicationCall){
    val parameters = call.receiveParameters()
    
    val username = parameters["username"] ?: "Input not received."
    val email = parameters["email"] ?: "Input not received."
    val rawPassword = parameters["password"] ?: "Input not received."

    val password = BCrypt.withDefaults().hashToString(8, rawPassword.toCharArray()) // only using level 8 for performance,
    val type_unconverted = parameters["usertype"] ?: "0"
    val type = type_unconverted.toInt()                            // I understand the varying levels of encryption.
    
    println("Username: $username, Email: $email, Password: $password") // for debug
    
    //collision check
    val userExists = doesCollide(username, email)
    var success = false

    if ( userExists ) { 
        call.respondText("User already exists. Please sign in instead.")
        return
    }
    else
    {
        if (type == 3){   PT.create(username, password, email)           }
        else{               User.create(type, username, password, email)   }
        success = true
    }

    redirectHandler( call, success, "/sign-in", "Failed to create user.")
}


fun authenticateUser(username: String, password: String): Boolean {

    return transaction {
        val user = User.find { Users.username eq username }.firstOrNull() ?: return@transaction false //instantly break if no user found
        val result = BCrypt.verifyer().verify(password.toCharArray(), user.password)

        result.verified
    }
}

fun authenticatePT(username: String, password: String): Boolean {
    return transaction {
        val user = PT.find { PTs.username eq username }.firstOrNull() ?: return@transaction false
        val result = BCrypt.verifyer().verify(password.toCharArray(), user.password)

        result.verified
    }
}

fun doesCollide(username: String, email: String): Boolean {
    return transaction {
        ( Users.selectAll().where {
            (Users.username eq username) or (Users.email eq email)
        }.firstOrNull() != null ) or
        ( PTs.selectAll().where {
            (PTs.username eq username) and (PTs.email eq email)
        }.firstOrNull() != null )
    }
}