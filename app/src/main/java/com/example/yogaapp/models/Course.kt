package com.example.yogaapp.models

data class Course(
    val id: Int = 0,
    val name: String,
    val dayOfWeek: String,
    val time: String,
    val capacity: Int,
    val duration: Int,
    val price: Double,
    val type: String,
    val description: String? = null
)