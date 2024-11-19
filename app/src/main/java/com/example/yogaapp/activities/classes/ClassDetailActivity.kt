package com.example.yogaapp.activities.classes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yogaapp.R
import com.example.yogaapp.database.DatabaseHelper
import com.example.yogaapp.models.ClassInstance

class ClassDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var classInstance: ClassInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_detail)

        dbHelper = DatabaseHelper(this)

        // Get the class instance ID passed from previous activity
        val classInstanceId = intent.getIntExtra("class_instance_id", -1)
        classInstance = dbHelper.getClassById(classInstanceId) ?: return

        val etClassId = findViewById<EditText>(R.id.etClassId)
        val etClassName = findViewById<EditText>(R.id.etClassName)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etTeacher = findViewById<EditText>(R.id.etTeacher)
        val etCourseId = findViewById<EditText>(R.id.etCourseId)

        // Set the class instance data in the EditText fields
        etClassId.setText(classInstance.id.toString())
        etClassName.setText(classInstance.className)
        etDate.setText(classInstance.date)
        etTeacher.setText(classInstance.teacher)
        etCourseId.setText(classInstance.courseId.toString())

        // Set the "Back" button behavior
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, ClassHomeActivity::class.java)
            startActivity(intent)
        }

        // Set the "Update" button behavior
        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            val updatedClassInstance = ClassInstance(
                id = classInstance.id,
                className = etClassName.text.toString(),
                date = etDate.text.toString(),
                teacher = etTeacher.text.toString(),
                courseId = classInstance.courseId // Retain the same course ID
            )

            val isUpdated = dbHelper.updateClass(updatedClassInstance)
            if (isUpdated) {
                Toast.makeText(this, getString(R.string.class_updated_success), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.class_updated_failure), Toast.LENGTH_SHORT).show()
            }
        }

        // Set the "Delete" button behavior
        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            // Ensure that the classInstance.id is not null
            classInstance.id?.let { id ->
                val isDeleted = dbHelper.deleteClass(id)
                if (isDeleted) {
                    Toast.makeText(this, getString(R.string.class_deleted_success), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.class_deleted_failure), Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, getString(R.string.class_id_not_found), Toast.LENGTH_SHORT).show()
            }
        }
    }
}