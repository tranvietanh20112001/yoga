package com.example.yogaapp.database


import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.yogaapp.models.Course
import android.database.Cursor
import android.util.Log
import com.example.yogaapp.models.ClassInstance
import com.google.firebase.firestore.FirebaseFirestore


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {



    companion object {
        private const val DATABASE_NAME = "yoga_app.db"
        private const val DATABASE_VERSION = 3

        // table course
        const val TABLE_COURSES = "courses"
        const val COLUMN_ID = "course_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DAY_OF_WEEK = "day_of_week"
        const val COLUMN_TIME = "time"
        const val COLUMN_CAPACITY = "capacity"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_PRICE = "price"
        const val COLUMN_TYPE = "type"
        const val COLUMN_DESCRIPTION = "description"

        const val TABLE_CLASS ="classes"
        const val CLASS_NAME = "class_name"
        const val COLUMN_CLASS_ID ="id"
        const val COURSE_ID = "courseId"
        const val DATE = "date"
                const val TEACHER = "teacher"
    }

    private val dbFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(db: SQLiteDatabase) {
        val createCoursesTable = """
            CREATE TABLE $TABLE_COURSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_DAY_OF_WEEK TEXT,
                $COLUMN_TIME TEXT,
                $COLUMN_CAPACITY INTEGER,
                $COLUMN_DURATION INTEGER,
                $COLUMN_PRICE REAL,
                $COLUMN_TYPE TEXT,
                $COLUMN_DESCRIPTION TEXT
            )
        """.trimIndent()
        db.execSQL(createCoursesTable)

        val createClassInstanceTable = """
            CREATE TABLE $TABLE_CLASS (
                $COLUMN_CLASS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COURSE_ID INTEGER NOT NULL,
                $CLASS_NAME TEXT NOT NULL,
                $DATE TEXT NOT NULL,
                $TEACHER TEXT NOT NULL,
                FOREIGN KEY($COURSE_ID) REFERENCES $TABLE_COURSES($COLUMN_ID)
            );
        """
        db.execSQL(createClassInstanceTable)

    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COURSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLASS")
        onCreate(db)
    }

    fun addCourse(course: Course): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, course.name)
            put(COLUMN_DAY_OF_WEEK, course.dayOfWeek)
            put(COLUMN_TIME, course.time)
            put(COLUMN_CAPACITY, course.capacity)
            put(COLUMN_DURATION, course.duration)
            put(COLUMN_PRICE, course.price)
            put(COLUMN_TYPE, course.type)
            put(COLUMN_DESCRIPTION, course.description)
        }
        val result = db.insert(TABLE_COURSES, null, values)
        db.close()

        if (result != -1L) {
            uploadCoursesToFirestore()
        }

        return result != -1L


    }


    fun getAllCourses(): MutableList<Course> {
        val courseList = mutableListOf<Course>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_COURSES", null)
        if (cursor.moveToFirst()) {
            do {
                val course = Course(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_WEEK)),
                    time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                    capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY)),
                    duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                courseList.add(course)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return courseList
    }

    fun getCourseById(id: Int): Course? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_COURSES,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        var course: Course? = null
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            val dayOfWeekIndex = cursor.getColumnIndex(COLUMN_DAY_OF_WEEK)
            val timeIndex = cursor.getColumnIndex(COLUMN_TIME)
            val capacityIndex = cursor.getColumnIndex(COLUMN_CAPACITY)
            val durationIndex = cursor.getColumnIndex(COLUMN_DURATION)
            val priceIndex = cursor.getColumnIndex(COLUMN_PRICE)
            val typeIndex = cursor.getColumnIndex(COLUMN_TYPE)
            val descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION)

            // Check if all column indices are valid (>= 0)
            if (nameIndex >= 0 && dayOfWeekIndex >= 0 && timeIndex >= 0 &&
                capacityIndex >= 0 && durationIndex >= 0 && priceIndex >= 0 &&
                typeIndex >= 0 && descriptionIndex >= 0) {

                val name = cursor.getString(nameIndex)
                val dayOfWeek = cursor.getString(dayOfWeekIndex)
                val time = cursor.getString(timeIndex)
                val capacity = cursor.getInt(capacityIndex)
                val duration = cursor.getInt(durationIndex)
                val price = cursor.getDouble(priceIndex)
                val type = cursor.getString(typeIndex)
                val description = cursor.getString(descriptionIndex)

                course = Course(id, name, dayOfWeek, time, capacity, duration, price, type, description)
            }
        }

        cursor.close()
        return course
    }


    fun deleteCourse(courseId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(
            TABLE_COURSES,
            "$COLUMN_ID = ?",
            arrayOf(courseId.toString())
        )

        if (result > 0) {
            dbFirestore.collection("courses")
                .document(courseId.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Course with ID $courseId successfully deleted from Firestore.")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error deleting course from Firestore: ${e.message}")
                }
        }

        db.close()
        return result > 0
    }



    fun updateCourse(course: Course): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, course.name)
            put(COLUMN_DAY_OF_WEEK, course.dayOfWeek)
            put(COLUMN_TIME, course.time)
            put(COLUMN_CAPACITY, course.capacity)
            put(COLUMN_DURATION, course.duration)
            put(COLUMN_PRICE, course.price)
            put(COLUMN_TYPE, course.type)
            put(COLUMN_DESCRIPTION, course.description)
        }

        val result = db.update(
            TABLE_COURSES,
            contentValues,
            "$COLUMN_ID = ?",
            arrayOf(course.id.toString())
        )

        if (result > 0) {
            val courseMap = mapOf(
                "name" to course.name,
                "dayOfWeek" to course.dayOfWeek,
                "time" to course.time,
                "capacity" to course.capacity,
                "duration" to course.duration,
                "price" to course.price,
                "type" to course.type,
                "description" to course.description
            )

            dbFirestore.collection("courses")
                .document(course.id.toString())
                .set(courseMap)
                .addOnSuccessListener {
                    Log.d(TAG, "Course with ID ${course.id} successfully updated in Firestore.")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error updating course in Firestore: ${e.message}")
                }
        }

        db.close()
        return result > 0
    }


    fun uploadCoursesToFirestore() {
        val courseList = getAllCourses()
        for (course in courseList) {
            val courseMap = hashMapOf(
                "name" to course.name,
                "dayOfWeek" to course.dayOfWeek,
                "time" to course.time,
                "capacity" to course.capacity,
                "duration" to course.duration,
                "price" to course.price,
                "type" to course.type,
                "description" to course.description
            )
            dbFirestore.collection("courses")
                .document(course.id.toString())
                .set(courseMap)
                .addOnSuccessListener {
                    Log.d(TAG,"SUCCESSFULL")
                }
                .addOnFailureListener { e ->
                    Log.d(TAG,"Error uploading course: ${e.message}")
                }
        }
    }

    fun insertClass(classInstance: ClassInstance): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(CLASS_NAME, classInstance.className)
            put(DATE, classInstance.date)
            put(TEACHER, classInstance.teacher)
            put(COURSE_ID, classInstance.courseId)
        }

        val id = db.insert(TABLE_CLASS, null, contentValues)

        if (id > 0) {
            // Sync with Firebase
            val dbFirebase = FirebaseFirestore.getInstance()
            val classData = hashMapOf(
                "className" to classInstance.className,
                "date" to classInstance.date,
                "teacher" to classInstance.teacher,
                "courseId" to classInstance.courseId
            )
            dbFirebase.collection("classes").document(id.toString()).set(classData)
        }

        db.close()
        return id
    }

    fun getClasses(): List<ClassInstance> {
        val classList = mutableListOf<ClassInstance>()

        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_CLASS"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID))
                val className = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_NAME))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(DATE))
                val teacher = cursor.getString(cursor.getColumnIndexOrThrow(TEACHER))
                val courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID))

                val classInstance = ClassInstance(
                    id = id,
                    className = className,
                    date = date,
                    teacher = teacher,
                    courseId = courseId
                )
                classList.add(classInstance)
            } while (cursor.moveToNext())
        }

        cursor.close()

        val dbFirebase = FirebaseFirestore.getInstance()
        dbFirebase.collection("classes").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id.toInt()
                    val className = document.getString("className") ?: ""
                    val date = document.getString("date") ?: ""
                    val teacher = document.getString("teacher") ?: ""
                    val courseId = document.getLong("courseId")?.toInt() ?: 0

                    val classInstance = ClassInstance(
                        id = id,
                        className = className,
                        date = date,
                        teacher = teacher,
                        courseId = courseId
                    )
                    if (classList.none { it.id == id }) {
                        classList.add(classInstance)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("getClasses", "Error getting classes from Firebase", exception)
            }

        db.close()
        return classList
    }

    fun getClassById(id: Int): ClassInstance? {
        var classInstance: ClassInstance? = null

        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_CLASS,
            null,
            "$COLUMN_CLASS_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_CLASS_ID)
            val classNameIndex = cursor.getColumnIndex(CLASS_NAME)
            val dateIndex = cursor.getColumnIndex(DATE)
            val teacherIndex = cursor.getColumnIndex(TEACHER)
            val courseIdIndex = cursor.getColumnIndex(COURSE_ID)

            classInstance = ClassInstance(
                id = cursor.getInt(idIndex),
                className = cursor.getString(classNameIndex),
                date = cursor.getString(dateIndex),
                teacher = cursor.getString(teacherIndex),
                courseId = cursor.getInt(courseIdIndex)
            )
        }

        cursor.close()

        if (classInstance == null) {
            val dbFirebase = FirebaseFirestore.getInstance()
            dbFirebase.collection("classes").document(id.toString())
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val className = document.getString("className") ?: ""
                        val date = document.getString("date") ?: ""
                        val teacher = document.getString("teacher") ?: ""
                        val courseId = document.getLong("courseId")?.toInt() ?: 0

                        classInstance = ClassInstance(
                            id = id,
                            className = className,
                            date = date,
                            teacher = teacher,
                            courseId = courseId
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("getClassById", "Error getting class from Firebase", exception)
                }
        }

        return classInstance
    }

    fun updateClass(classInstance: ClassInstance): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(CLASS_NAME, classInstance.className)
            put(DATE, classInstance.date)
            put(TEACHER, classInstance.teacher)
            put(COURSE_ID, classInstance.courseId)
        }

        val result = db.update(
            TABLE_CLASS,
            contentValues,
            "$COLUMN_CLASS_ID = ?",
            arrayOf(classInstance.id.toString())
        )

        if (result > 0) {
            val dbFirebase = FirebaseFirestore.getInstance()
            dbFirebase.collection("classes").document(classInstance.id.toString())
                .set(mapOf(
                    "className" to classInstance.className,
                    "date" to classInstance.date,
                    "teacher" to classInstance.teacher,
                    "courseId" to classInstance.courseId
                ))
                .addOnSuccessListener {
                    Log.d("updateClass", "Class updated in Firebase successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("updateClass", "Error updating class in Firebase", exception)
                }
        }

        db.close()
        return result > 0
    }

    fun deleteClass(classInstanceId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(
            TABLE_CLASS,
            "$COLUMN_CLASS_ID = ?",
            arrayOf(classInstanceId.toString())
        )

        if (result > 0) {
            val dbFirebase = FirebaseFirestore.getInstance()
            dbFirebase.collection("classes").document(classInstanceId.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d("deleteClass", "Class deleted from Firebase successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("deleteClass", "Error deleting class from Firebase", exception)
                }
        }

        db.close()
        return result > 0
    }



}
