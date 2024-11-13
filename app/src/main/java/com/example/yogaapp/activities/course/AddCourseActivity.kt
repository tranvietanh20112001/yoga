package com.example.yogaapp.activities.course

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.yogaapp.R
import com.example.yogaapp.database.DatabaseHelper
import com.example.yogaapp.models.Course

class AddCourseActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_course)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, CourseHomeActivity::class.java)
            startActivity(intent)
        }
        dbHelper = DatabaseHelper(this)

        val etCourseName = findViewById<EditText>(R.id.etCourseName)
        val spinnerDayOfWeek = findViewById<Spinner>(R.id.spinnerDayOfWeek)
        val etTime = findViewById<EditText>(R.id.etTime)
        val etCapacity = findViewById<EditText>(R.id.etCapacity)
        val etDuration = findViewById<EditText>(R.id.etDuration)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val spinnerType = findViewById<Spinner>(R.id.spinnerType)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnSaveCourse = findViewById<Button>(R.id.btnSaveCourse)

        // Initialize Day of Week Spinner
        val daysOfWeek = resources.getStringArray(R.array.days_of_week)
        spinnerDayOfWeek.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)

        // Initialize Type Spinner
        val courseTypes = resources.getStringArray(R.array.course_types)
        spinnerType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseTypes)

        btnSaveCourse.setOnClickListener {
            val courseName = etCourseName.text.toString()
            val dayOfWeek = spinnerDayOfWeek.selectedItem.toString()
            val time = etTime.text.toString()
            val capacity = etCapacity.text.toString().toIntOrNull() ?: 0
            val duration = etDuration.text.toString().toIntOrNull() ?: 0
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val type = spinnerType.selectedItem.toString()
            val description = etDescription.text.toString()

            if (courseName.isNotBlank() && dayOfWeek.isNotBlank() && time.isNotBlank()
                && capacity > 0 && duration > 0 && price > 0.0 && type.isNotBlank()
            ) {
                val newCourse = Course(
                    name = courseName,
                    dayOfWeek = dayOfWeek,
                    time = time,
                    capacity = capacity,
                    duration = duration,
                    price = price,
                    type = type,
                    description = description
                )

                val result = dbHelper.addCourse(newCourse)
                if (result) {
                    Toast.makeText(this, "Course saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save course", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
