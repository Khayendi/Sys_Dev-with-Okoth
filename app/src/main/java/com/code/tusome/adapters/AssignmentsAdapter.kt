package com.code.tusome.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.code.tusome.R
import com.code.tusome.databinding.UnitItemBinding
import com.code.tusome.models.Assignment

class AssignmentsAdapter(private val list: List<Assignment>):RecyclerView.Adapter<AssignmentsAdapter.AssignmentViewHolder>() {
    private lateinit var listener:OnItemLongCLick
    interface OnItemLongCLick{
        fun onItemLongClick(position: Int)
    }
    fun setOnItemLongClick(listener:OnItemLongCLick){
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder =
        AssignmentViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.unit_item, parent, false),
        )

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) =
        holder.bind(list[position])

    override fun getItemCount(): Int = list.size
    inner class AssignmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = UnitItemBinding.bind(view)
        init {
            binding.root.setOnLongClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(adapterPosition)
                }
                true
            }
        }
        fun bind(exam: Assignment) {
            binding.coursesTitleTv.text = exam.unitName
            binding.coursesDescriptionTv.text = exam.description
            binding.schoolTv.text = exam.issueDate
            binding.departmentTv.text = exam.dueDate
        }
    }
}