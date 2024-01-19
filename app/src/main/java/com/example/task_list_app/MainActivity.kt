package com.example.task_list_app

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


//Meant for Api 31 or less
// Reminder: Permission for notificaitons must be granted on api 33
class MainActivity : AppCompatActivity() {
    private val dataSQL = DataHelper(this)
    private val layout = LinearLayoutManager(this)
    private val EDIT_CODE_SUCCESS = 100
    private val EDIT_TASK_TITLE = "EDIT_TASK"
    private val NEW_TASK_TITLE = "NEW_TASK"
    private lateinit var addTaskBtn: Button
    private lateinit var dueListRV: RecyclerView
    private lateinit var listDueTasks: MutableList<TaskInfo>
    private lateinit var notification: NotificationClass
    private lateinit var customAdapter: CustomRecyclerViewAdapter
    private lateinit var startActivityForReturn: ActivityResultLauncher<Intent>
    private var editAdapterPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //for notifications, calls on the channel
        //mutable list that gets all data from SQl
        //button id, recyclerview id,
        // recylerview layout, recylerview divider, adapter for recyclerview
        notification = NotificationClass()
        listDueTasks = dataSQL.getAllTask()
        addTaskBtn = findViewById(R.id.main_button_tlbr)
        dueListRV = findViewById(R.id.due_list_rv)
        dueListRV.layoutManager = layout
        dueListRV.addItemDecoration(DividerItemDecoration(baseContext , layout.orientation))
        customAdapter = CustomRecyclerViewAdapter(this , listDueTasks)

        //for adding and editing tasks in recyclerview
        returnIntentInfo()

        //for editing, sets up an click in the viewholder,
        // sends taskinfo to NewTask, stores in adapter position
        //TaskInfo will be returned in returnIntentInfo()
        customAdapter.onItemClick = { taskObj , position ->
            val intent = Intent(this , NewTask::class.java)
            intent.putExtra(EDIT_TASK_TITLE , taskObj)
            editAdapterPosition = position
            startActivityForReturn.launch(intent)
        }

        //sets the adapter to the RV
        dueListRV.adapter = customAdapter

        //for deleting tasks in RV by swiping
        setSwipeAction()


        //if addTaskBtn is clicked, will go to NewTask
        //TaskInfo will be returned in returnIntentInfo()
        addTaskBtn.setOnClickListener {
            startActivityForReturn.launch(Intent(this , NewTask::class.java))
        }
    }

    //gets info for editing/new tasks (returns from NewTask)
    //* for parcelable classes
    private fun returnIntentInfo(){
        //use for apis less than 30 (current api 26) (for getting parcelable class)
        startActivityForReturn =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                //if editing, else if creating new task
                if (result.resultCode == EDIT_CODE_SUCCESS) {
                    //gets parcelable
                    val resultTaskObj = result.data?.getParcelableExtra<TaskInfo>(EDIT_TASK_TITLE)

                    //edits task in RV and SQl, resets notif
                    if (resultTaskObj != null && editAdapterPosition != null) {
                        customAdapter.editItem(resultTaskObj , editAdapterPosition!!)
                        notification.scheduleNotif(this , resultTaskObj)
                    }
                } else if (result.resultCode == Activity.RESULT_OK) {
                    //gets class
                    val resultTaskObj = result.data?.getParcelableExtra<TaskInfo>(NEW_TASK_TITLE)


                    if (resultTaskObj != null) {
                        //creates new task in RV and SQl, sets notif
                        customAdapter.addItem(resultTaskObj)
                        notification.scheduleNotif(this , resultTaskObj)
                    }
                }
            }
    }

    //sets up swipe actions for the recyclerView using ItemTouchHelper
    //also deletes items in RV and SQl permanently
    private fun setSwipeAction(){

        val slidingItemRV = object : ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.RIGHT) {
            //recyclerview will not move items
            override fun onMove(
                recyclerView: RecyclerView ,
                viewHolder: RecyclerView.ViewHolder ,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            //sets a swipe motion to the right
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder , direction: Int) {
                //cancels notification
                notification.cancelNotif(
                    viewHolder.itemView.context ,
                    listDueTasks[viewHolder.adapterPosition]
                )

                //deletes item from RV and SQL
                customAdapter.removeItem(viewHolder.adapterPosition)
            }


        }

        //attaches to RV
        ItemTouchHelper(slidingItemRV).attachToRecyclerView(dueListRV)
    }

}





