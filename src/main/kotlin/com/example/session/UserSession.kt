package com.example.session

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val id: String, val username: String)