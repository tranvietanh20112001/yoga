package com.example.yogaapp.activities.course

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaapp.MainActivity
import com.example.yogaapp.R
import com.example.yogaapp.adapters.CourseAdapter
import com.example.yogaapp.database.DatabaseHelper
import com.example.yogaapp.models.Course

class CourseHomeActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_home)

        dbHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get list of all courses from the database
        val courseList = dbHelper.getAllCourses()

        // Set the adapter for the RecyclerView
        courseAdapter = CourseAdapter(this, courseList)
        recyclerView.adapter = courseAdapter

        // Handle "Add Course" button click
        val btnAddCourse = findViewById<Button>(R.id.btnAddCourse)
        btnAddCourse.setOnClickListener {
            val intent = Intent(this, AddCourseActivity::class.java)
            startActivity(intent)
        }

        // Handle back button click to navigate to the main activity
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }




    // Update course list when returning to the activity
    override fun onResume() {
        super.onResume()
        val courseList = dbHelper.getAllCourses()
        courseAdapter.updateCourses(courseList)
    }
}
