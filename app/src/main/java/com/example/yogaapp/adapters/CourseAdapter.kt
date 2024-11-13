package com.example.yogaapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaapp.R
import com.example.yogaapp.activities.course.CourseDetailActivity
import com.example.yogaapp.models.Course

class CourseAdapter(private val context: Context, private var courseList: List<Course>) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]
        holder.bind(course)
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    fun updateCourses(newCourseList: List<Course>) {
        courseList = newCourseList
        notifyDataSetChanged()
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCourseName: TextView = itemView.findViewById(R.id.tvCourseName)
        private val tvCourseType: TextView = itemView.findViewById(R.id.tvCourseType)
        fun bind(course: Course) {
            tvCourseName.text = course.name
            tvCourseType.text = course.type
            itemView.setOnClickListener {
                val intent = Intent(context, CourseDetailActivity::class.java)
                intent.putExtra("course_id", course.id)
                context.startActivity(intent)
            }
        }
    }
}
