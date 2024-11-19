package com.example.yogaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaapp.R
import com.example.yogaapp.models.ClassInstance

class ClassAdapter(
    private val classList: List<ClassInstance>,
    private val onClick: (ClassInstance) -> Unit
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val classInstance = classList[position]
        holder.bind(classInstance)
    }

    override fun getItemCount(): Int {
        return classList.size
    }

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvClassName: TextView = itemView.findViewById(R.id.tvClassName)
        private val tvClassTeacher: TextView = itemView.findViewById(R.id.tvClassTeacher)
        fun bind(classInstance: ClassInstance) {
            tvClassName.text = classInstance.className
            tvClassTeacher.text = classInstance.teacher
            itemView.setOnClickListener {
                onClick(classInstance)
            }
        }
    }


}