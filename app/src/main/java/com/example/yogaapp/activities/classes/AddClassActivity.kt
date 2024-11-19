package com.example.yogaapp.activities.classes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yogaapp.R
import com.example.yogaapp.activities.course.CourseHomeActivity
import com.example.yogaapp.database.DatabaseHelper
import com.example.yogaapp.models.ClassInstance

class AddClassActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spinnerCourse: Spinner
    private var courseIdMap: MutableMap<String, Int> = mutableMapOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_class)

        dbHelper = DatabaseHelper(this)
        spinnerCourse = findViewById(R.id.spinnerCourse)
        populateCourseSpinner()

        val btnSaveClass: Button = findViewById(R.id.btnSaveClass)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, CourseHomeActivity::class.java)
            startActivity(intent)
        }
        dbHelper = DatabaseHelper(this)

        btnSaveClass.setOnClickListener {
            val selectedCourseName = spinnerCourse.selectedItem.toString()
            val courseId = courseIdMap[selectedCourseName] ?: return@setOnClickListener
            val className = findViewById<EditText>(R.id.etClassName).text.toString()
            val date = findViewById<EditText>(R.id.etClassDate).text.toString()
            val teacher = findViewById<EditText>(R.id.etClassTeacher).text.toString()

            val classInstance = ClassInstance(className= className,date = date, teacher = teacher, courseId = courseId)
            dbHelper.insertClass(classInstance)

            Toast.makeText(this, "Class saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun populateCourseSpinner() {
        val courses = dbHelper.getAllCourses()
        val courseNames = courses.map { it.name }
        for (course in courses) {
            courseIdMap[course.name] = course.id ?: 0
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourse.adapter = adapter
    }
}