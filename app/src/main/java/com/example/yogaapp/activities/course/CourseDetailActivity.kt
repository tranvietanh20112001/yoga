package com.example.yogaapp.activities.course

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yogaapp.R
import com.example.yogaapp.database.DatabaseHelper
import com.example.yogaapp.models.Course

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var course: Course

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_detail)

        dbHelper = DatabaseHelper(this)

        val courseId = intent.getIntExtra("course_id", -1)
        course = dbHelper.getCourseById(courseId) ?: return

        val etCourseId = findViewById<EditText>(R.id.etCourseId)
        val etCourseName = findViewById<EditText>(R.id.etCourseName)
        val etDayOfWeek = findViewById<EditText>(R.id.etDayOfWeek)
        val etTime = findViewById<EditText>(R.id.etTime)
        val etCapacity = findViewById<EditText>(R.id.etCapacity)
        val etDuration = findViewById<EditText>(R.id.etDuration)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etType = findViewById<EditText>(R.id.etType)
        val etDescription = findViewById<EditText>(R.id.etDescription)


        etCourseId.setText(course.id.toString())
        etCourseName.setText(course.name)
        etDayOfWeek.setText(course.dayOfWeek)
        etTime.setText(course.time)
        etCapacity.setText(course.capacity.toString())
        etDuration.setText(course.duration.toString())
        etPrice.setText(course.price.toString())
        etType.setText(course.type)
        etDescription.setText(course.description)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, CourseHomeActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            val updatedCourse = Course(
                id = course.id,
                name = etCourseName.text.toString(),
                dayOfWeek = etDayOfWeek.text.toString(),
                time = etTime.text.toString(),
                capacity = etCapacity.text.toString().toInt(),
                duration = etDuration.text.toString().toInt(),
                price = etPrice.text.toString().toDouble(),
                type = etType.text.toString(),
                description = etDescription.text.toString()
            )

            val isUpdated = dbHelper.updateCourse(updatedCourse)
            if (isUpdated) {
                Toast.makeText(this, getString(R.string.course_updated_success), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.course_updated_failure), Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            val isDeleted = dbHelper.deleteCourse(course.id)
            if (isDeleted) {
                Toast.makeText(this, getString(R.string.course_deleted_success), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.course_deleted_failure), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
