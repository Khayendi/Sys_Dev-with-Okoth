package com.code.tusome.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Course(
    val uid: String,
    val courseCode: String,
    val units: ArrayList<CourseUnit>,
    val department: String,
    val school: String
)

data class CourseUnit(
    val uid: String,
    val instructor: String,
    val year: String,
    val exam: ArrayList<Exam>,
    val cat: ArrayList<Cat>
)

data class Cat(
    val uid: String,
    val invigilator: String,
    val date: Long,
    val room: String,
    val duration: String
)

data class Exam(
    val uid: String,
    val invigilator: String,
    val date: Long,
    val noOfStudents: String,
    val room: String,
    val duration: String
)


data class Assignment(
    val uid: String,
    val unitName: String,
    val name: String,
    val issueDate: Long,
    val dueDate: Long,
    val submitted: Boolean
)

@Entity(tableName = "assignment")
data class AssignmentDB(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "unit_name") val unitName: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "date_issued") val issueDate: Long,
    @ColumnInfo(name = "due_date") val dueDate: Long,
    @ColumnInfo(name = "submitted") val submitted: Boolean
)