package com.example.database

//SQL
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.selectAll

//Hashing
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.CurrentWorkoutSession

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
    
    var isValid = authenticateUser(username, password) // Check if login is correct

    if (!isValid)
    {
        isValid = authenticatePT(username, password) //Double check if user is a PT
        isPT = isValid     //isValid can only be true now if the account wasn't a valid user but is a valid PT now
    }
    if (isValid) {         //If valid now sign-in and begin user session
        transaction {
            val userID: Int? = if (isPT) {
                getPTIdByUsername(username)
            } else {
                getUserIdByUsername(username)
            }
            call.sessions.set(UserSession(id=userID.toString(), username=username))
            call.sessions.clear<CurrentWorkoutSession>()
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
    val type_unconverted = parameters["usertype"] ?: "0"                            // I understand the varying levels of encryption.
    
    val type = type_unconverted.toIntOrNull() ?: 0  //Have to convert type into an integer, if no input received take 0 ( which is an invalid type )

    println("Username: $username, Email: $email, Password: $password") // prints to terminal for debugging
    
    //collision check
    val userExists = doesCollide(username, email)   //Checks if username/email have been used before
    var success = false                             //kinda redundant but I can't be bothered to change it

    if ( userExists ) { 
        call.respondText("User already exists. Please sign in instead.")    //returns error if user exists
        return
    }
    else    //Creates new user if user doesn't exist ( the only other case)
    {
        if (type == 3){   PT.create(username, password, email)           }      //type 3 accounts are PT's 
        else{             User.create(type, username, password, email)   }      //type 1-2 are casual/competitor
        success = true  //technically = !userExists but I'll stick to it for now
    }

    redirectHandler( call, success, "/sign-in", "Failed to create user.")
}


fun authenticateUser(username: String, password: String): Boolean {

    return transaction {
        val user = User.find { Users.username eq username }.firstOrNull() ?: return@transaction false // break found
        val result = BCrypt.verifyer().verify(password.toCharArray(), user.password)                  // if user found compare hash

        result.verified
    }
}

fun authenticatePT(username: String, password: String): Boolean {
    return transaction {
        val user = PT.find { PTs.username eq username }.firstOrNull() ?: return@transaction false   // same as above
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
            (PTs.username eq username) or (PTs.email eq email)
        }.firstOrNull() != null )
    }
}