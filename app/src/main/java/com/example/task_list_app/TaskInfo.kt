package com.example.task_list_app

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskInfo(
    private var taskName: String,
    private var year: Int,
    private var month: Int,
    private var day: Int,
    private var hour: Int,
    private var min: Int,
    private var sqlId: Int? = null
) : Parcelable
{


    //MUTATOR
    fun setTask(taskName: String){
        this.taskName = taskName
    }


    fun setYear(year: Int){
        this.year = year
    }


    fun setMonth(month: Int){
        this.month = month
    }


    fun setDay(day: Int){
        this.day = day
    }


    fun setHour(hour: Int){
        this.hour = hour
    }


    fun setMin(min: Int){
        this.min = min
    }


    fun setId(id: Int){
        sqlId = id
    }


    //ACCESSOR
    fun getTask(): String{
        return taskName
    }


    fun getYear(): Int{
        return year
    }


    fun getMonth(): Int{
        return month
    }


    fun getDay(): Int{
        return day
    }


    fun getHour(): Int{
        return hour
    }


    fun getMin(): Int{
        return min
    }


    fun getId(): Int? {
        return sqlId
    }


    //displays date and time
    override fun toString(): String {
        var monthStr = "$month"
        var dayStr = "$day"
        var minStr = "$min"


        if(month < 10)
            monthStr = "0$monthStr"


        if(day < 10)
            dayStr = "0$dayStr"


        if(min < 10)
            minStr = "0$minStr"




        return "DUE DATE: $monthStr/$dayStr/$year $hour:$minStr"
    }




}


