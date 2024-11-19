package com.example.yogaapp.models

data class ClassInstance(
    val id: Int? = 0,
    val className : String,
    val date: String,
    val teacher: String,
    val courseId: Int,
)