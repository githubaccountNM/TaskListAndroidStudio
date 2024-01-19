package com.example.task_list_app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DataHelper(
    context: Context
) : SQLiteOpenHelper(context, "${DATABASE_NAME}", null, DATABASE_VERSION) {

    private companion object {
        private const val DATABASE_NAME = "TASK_LIST_DB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "TASK_TABLE"
        private const val TASK_ID = "TASK_ID"
        private const val TASK_NAME = "TASK_NAME"
        private const val YEAR = "YEAR"
        private const val MONTH = "MONTH"
        private const val DAY = "DAY"
        private const val HOUR = "HOUR"
        private const val MINUTE = "MINUTE"
        // private const val REPETITION = "REPETITION"
        private const val SQL_PRIMARY_TABLE =
            "CREATE TABLE ${TABLE_NAME} (" +
                    "${TASK_ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "${TASK_NAME} TEXT," +
                    "${YEAR} INTEGER," +
                    "${MONTH} INTEGER," +
                    "${DAY} INTEGER," +
                    "${HOUR} INTEGER," +
                    "${MINUTE} INTEGER)"


    }




   //generates all rows in swl
    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(SQL_PRIMARY_TABLE)
        }
        catch(error: Exception){
            Log.e("Database_Exec_ERROR", error.message ?: "ERROR: Cannot execute SQL")
        }
    }




    //when version changes
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // if(oldVersion < 2){
        //   db.execSQL("ALTER TABLE ${TABLE_NAME} ADD COLUMN ${REPETITION} INTEGER")
        //}
    }


    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
/*  TODO :
   if(oldVersion > 1){
          db.execSQL("CREATE TABLE NEW_TABLE(${TASK_ID} INTEGER PRIMARY KEY AUTOINCREMENT, ${TASK_NAME} TEXT,${START_DATE} TEXT,${START_TIME} TEXT,${END_DATE} TEXT,${END_TIME} TEXT)")
          db.execSQL("INSERT INTO NEW_TABLE SELECT ${TASK_ID}, ${TASK_NAME}, ${START_DATE}, ${START_TIME}, ${END_DATE}, ${END_TIME} FROM ${TABLE_NAME}")
          db.execSQL("DROP TABLE ${TABLE_NAME}")
          db.execSQL("ALTER TABLE NEW_TABLE RENAME TO ${TABLE_NAME}")
      }*/
    }

    //write new row to database
    fun addTask(TaskObj : TaskInfo): Int{

        var db: SQLiteDatabase? = null
        var idPosition = -1L


        try {
           db = this.writableDatabase
           val cv = ContentValues()


           cv.put(TASK_NAME, TaskObj.getTask())
           cv.put(YEAR, TaskObj.getYear())
           cv.put(MONTH, TaskObj.getMonth())
           cv.put(DAY, TaskObj.getDay())
           cv.put(HOUR, TaskObj.getHour())
           cv.put(MINUTE, TaskObj.getMin())


           idPosition = db.insert(TABLE_NAME, null, cv)
        }
        catch(error: Exception){
           Log.e("Database/ContentValue_ERROR", error.message ?: "Cannot open/insert in SQL")
        }
        finally {
           db?.close()
        }

        //returns id
        return (idPosition.toInt())
    }


    //deletes all rows of one id in database
    fun deleteTask(TaskObj: TaskInfo): Boolean{
        var db: SQLiteDatabase? = null
        var rowsDeleted = 0


        try {
           db =  this.writableDatabase
           rowsDeleted = db.delete(TABLE_NAME, "${TASK_ID}=?", arrayOf(TaskObj.getId().toString()))
        }
        catch(error: Exception){
           Log.e("Database_Delete_ERROR", error.message ?: "Cannot open/delete in SQL")
        }
        finally{
           db?.close()
        }


        return (rowsDeleted == 0)
    }


    //updates all rows for one id in database
    fun editTask(TaskObj: TaskInfo): Boolean{
        var db: SQLiteDatabase? = null
        val whereArgsArray = arrayOf<String>(TaskObj.getId().toString())
        var rowsChanged = 0


        try {
           val cv = ContentValues()
           cv.put(TASK_NAME, TaskObj.getTask())
           cv.put(YEAR,TaskObj.getYear())
           cv.put(MONTH,  TaskObj.getMonth())
           cv.put(DAY,TaskObj.getDay())
           cv.put(HOUR, TaskObj.getHour())
           cv.put(MINUTE, TaskObj.getMin())
           db =  this.writableDatabase


           rowsChanged = db.update(TABLE_NAME, cv,"$TASK_ID=?", whereArgsArray)
        }
        catch(error: Exception){
           Log.e("Database/ContentValue_ERROR", error.message ?: "Cannot open/update in SQL")
        }
        finally{
           db?.close()
        }


        return (rowsChanged == 0)
    }


    //gets all tasks from database and stores into a mutable lise to return to MainActivity
    fun getAllTask(): MutableList<TaskInfo> {
        var db: SQLiteDatabase? = null
        val taskDateList = mutableListOf<TaskInfo>()
        var cursor: Cursor? = null




        try {
           db = this.readableDatabase
           cursor = db.rawQuery("SELECT * FROM ${TABLE_NAME}", null)


           while (cursor.moveToNext()) {
               val id = cursor.getInt(0)
               val taskName = cursor.getString(1)
               val year = cursor.getInt(2)
               val month = cursor.getInt(3)
               val day = cursor.getInt(4)
               val hour = cursor.getInt(5)
               val min = cursor.getInt(6)




               val taskObj = TaskInfo(taskName, year, month, day, hour, min, id)
               taskDateList.add(taskObj)
           }
        }
        catch(error: Exception){
           Log.e("Cursor/Database_ERROR", error.message ?: "Cannot open/find information in SQL")
        }
        finally{
           cursor?.close()
           db?.close()
        }


        return taskDateList


    }




}

