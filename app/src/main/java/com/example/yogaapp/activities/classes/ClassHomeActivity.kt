package com.example.yogaapp.activities.classes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaapp.MainActivity
import com.example.yogaapp.R
import com.example.yogaapp.adapters.ClassAdapter
import com.example.yogaapp.database.DatabaseHelper

class ClassHomeActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvClassList: RecyclerView
    private lateinit var classAdapter: ClassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_home)
        dbHelper = DatabaseHelper(this)

        rvClassList = findViewById(R.id.rvClassList)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnAddClass = findViewById<Button>(R.id.btnAddClass)
        btnAddClass.setOnClickListener {
            val intent = Intent(this, AddClassActivity::class.java)
            startActivity(intent)
        }

        val classList = dbHelper.getClasses()

        classAdapter = ClassAdapter(classList) { classInstance ->
            val intent = Intent(this, ClassDetailActivity::class.java)
            intent.putExtra("class_instance_id", classInstance.id)
            startActivity(intent)
        }

        rvClassList.layoutManager = LinearLayoutManager(this)
        rvClassList.adapter = classAdapter
    }
}