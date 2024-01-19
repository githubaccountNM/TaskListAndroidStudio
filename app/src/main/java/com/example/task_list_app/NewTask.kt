package com.example.task_list_app


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import java.util.Calendar


class NewTask : AppCompatActivity() {
    private val EDIT_CODE_SUCCESS = 100
    private val EDIT_TASK_TITLE = "EDIT_TASK"
    private val NEW_TASK_TITLE = "NEW_TASK"
    private val TEXT_EMPTY ="This field is required."
    private val TEXT_ERR_MESSAGE = "Please fill out all required fields."
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var createTaskBtn: Button
    private lateinit var taskText: EditText
    private lateinit var dueDatepick: DatePicker
    private lateinit var dueTimepick: TimePicker
    private var currYear : Int = Calendar.YEAR
    private var currDay : Int = 1
    private var currMonth : Int = 1
    private var currHour: Int = 1
    private var currMin: Int = 0
    private var editTaskObj: TaskInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)


        //for tool bar, for up button (previous button), action handled in onOptionsItemSelected
        toolbar = findViewById(R.id.new_task_tlbr)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //for button, edit text, datepicker, timepicker (24hr View)
        // , and parcelable class TaskInfo from Main Activity
        createTaskBtn = findViewById(R.id.new_task_btn)
        taskText = findViewById(R.id.event_name_text)
        dueDatepick = findViewById(R.id.due_datepicker)
        dueTimepick = findViewById(R.id.due_timepicker)
        dueTimepick.setIs24HourView(true)
        editTaskObj = intent.getParcelableExtra(EDIT_TASK_TITLE)

        //if editTaskObj is not null, sets date/time picker to date in taskobj
        //else sets to current date
        //used when the user does not change date/time picker
        if(editTaskObj != null){
            taskText.setText(editTaskObj!!.getTask())
            currYear = editTaskObj!!.getYear()
            currMonth = editTaskObj!!.getMonth()
            currDay = editTaskObj!!.getDay()
            currHour =  editTaskObj!!.getHour()
            currMin = editTaskObj!!.getMin()

            dueDatepick.updateDate(currYear, currMonth - 1, currDay)
            dueTimepick.hour = currHour
            dueTimepick.minute = currMin
        }
        else{
            val calendar = Calendar.getInstance()
            currYear = calendar.get(Calendar.YEAR)
            currDay = calendar.get(Calendar.DAY_OF_MONTH)
            currMonth = calendar.get(Calendar.MONTH) + 1
            currHour =  calendar.get(Calendar.HOUR_OF_DAY)
            currMin = calendar.get(Calendar.MINUTE)
        }


        //for date/time pickers and for create event button
        setPicker()
        setCreateBtn()

    }


    //used to change date/time on datepicker/timepicker
    private fun setPicker(){
        //for api 26 or higher use setOnDateChangedListener, else use init
        dueDatepick.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            currYear = year
            currMonth = monthOfYear + 1
            currDay = dayOfMonth
        }

        dueTimepick.setOnTimeChangedListener{ _, hourOfDay, minute ->
            currHour = hourOfDay
            currMin = minute
        }

    }

    private fun setCreateBtn(){
        //when user clicks on create task
        //error if user did not insert any char
        //sends TaskInfo back to MainActivity
        createTaskBtn.setOnClickListener {
            if (TextUtils.isEmpty(taskText.text.trim())) {
                taskText.setError(TEXT_EMPTY)
                Toast.makeText(this , TEXT_ERR_MESSAGE, Toast.LENGTH_SHORT)
                    .show()
            } else {
                val resultIntent = Intent()

                //if editTaskObj was correctly initalized, sends back using setResult
                //else creates new TaskInfo class and sends it back
                if (editTaskObj != null) {
                    editTaskObj!!.setTask(taskText.text.toString().take(25))
                    editTaskObj!!.setYear(currYear)
                    editTaskObj!!.setMonth(currMonth)
                    editTaskObj!!.setDay(currDay)
                    editTaskObj!!.setHour(currHour)
                    editTaskObj!!.setMin(currMin)
                    resultIntent.putExtra(EDIT_TASK_TITLE , editTaskObj)
                    setResult(EDIT_CODE_SUCCESS , resultIntent)
                } else {
                    resultIntent.putExtra(
                        NEW_TASK_TITLE,
                        TaskInfo(
                            taskText.text.toString().take(25),
                            currYear ,
                            currMonth ,
                            currDay ,
                            currHour ,
                            currMin
                        )
                    )
                    setResult(Activity.RESULT_OK , resultIntent)
                }


                //goes back
                finish()

            }
        }
    }

    //back button, for toolbar (must override onOptionsItemSelected)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            //id of up button
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}




