package com.example.yogaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.yogaapp.activities.classes.ClassHomeActivity
import com.example.yogaapp.activities.course.CourseHomeActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnManageCourses = findViewById<Button>(R.id.btnManageCourses)
        val btnManageClasses = findViewById<Button>(R.id.btnManageClasses)

        btnManageCourses.setOnClickListener {
            val intent = Intent(this, CourseHomeActivity::class.java)
            startActivity(intent)
        }

        btnManageClasses.setOnClickListener {
            val intent = Intent(this, ClassHomeActivity::class.java)
            startActivity(intent)
        }
    }
}