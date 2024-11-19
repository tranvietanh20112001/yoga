package com.example.yogaapp.activities.classes

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import java.util.*

class AddClassActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spinnerCourse: Spinner
    private lateinit var etClassDate: EditText
    private var courseIdMap: MutableMap<String, Int> = mutableMapOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_class)

        dbHelper = DatabaseHelper(this)
        spinnerCourse = findViewById(R.id.spinnerCourse)
        etClassDate = findViewById(R.id.etClassDate)

        populateCourseSpinner()

        // Back Button
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, CourseHomeActivity::class.java)
            startActivity(intent)
        }

        // Save Class Button
        val btnSaveClass: Button = findViewById(R.id.btnSaveClass)
        btnSaveClass.setOnClickListener {
            val selectedCourseName = spinnerCourse.selectedItem.toString()
            val courseId = courseIdMap[selectedCourseName] ?: return@setOnClickListener
            val className = findViewById<EditText>(R.id.etClassName).text.toString()
            val date = etClassDate.text.toString()
            val teacher = findViewById<EditText>(R.id.etClassTeacher).text.toString()

            if (className.isEmpty() || date.isEmpty() || teacher.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val classInstance = ClassInstance(className = className, date = date, teacher = teacher, courseId = courseId)
            dbHelper.insertClass(classInstance)

            Toast.makeText(this, "Class saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Set up the Date Picker Dialog for class date
        etClassDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format the date as you want (e.g., "yyyy-MM-dd")
                    val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    etClassDate.setText(formattedDate)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
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
