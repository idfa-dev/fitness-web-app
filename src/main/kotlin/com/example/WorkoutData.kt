package com.example

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutData(
    val day: String,
    val minutes: Int,
    val type: String
)