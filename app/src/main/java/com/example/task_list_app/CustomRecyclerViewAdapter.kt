package com.example.task_list_app

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerViewAdapter(
    private val context: Context,
    private val mutableList: MutableList<TaskInfo>
): RecyclerView.Adapter<CustomRecyclerViewAdapter.TaskViewHolder>(){

    //to do a specific action when RV is clicked
    var onItemClick: ((TaskInfo, Int) -> Unit)? = null


    inner class TaskViewHolder(
        private val itemView: View
    ) : RecyclerView.ViewHolder(itemView) {


        val taskName: TextView = itemView.findViewById(R.id.task_name_rv)
        val dueDate: TextView = itemView.findViewById(R.id.due_date_rv)


        //gives data back to lamba for action
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mutableList[adapterPosition], adapterPosition)
            }
        }


    }




    //display for column of recyclerview using custom made layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.recycler_view_layout, parent, false)
        return (TaskViewHolder(view))
    }


    //binds information to layout
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskName.setText(mutableList[position].getTask())
        holder.dueDate.setText(mutableList[position].toString())
    }




    //number of items inside mutableList
    override fun getItemCount(): Int {
        return mutableList.size
    }


    //adds item in RV and calls datahelper to add item in sql
    //sets id or gives error
    fun addItem(taskObj: TaskInfo){
        mutableList.add(taskObj)
        val data = DataHelper(context)
        val taskId = data.addTask(taskObj)


        if(taskId == -1){
            Log.e("SQL_ADD_ERROR", "ERROR: Task was not stored.")
        }
        else
            taskObj.setId(taskId)


        notifyItemInserted(mutableList.size - 1)

    }

    //removes TaskInfo in RV and calls datahelper to remove TaskInfo in sql
    //gives error if not removed in sql
    fun removeItem(position: Int){
        val data = DataHelper(context)
        val isNotRemoved = data.deleteTask(mutableList[position])


        mutableList.removeAt(position)
        notifyItemRemoved(position)


        if(isNotRemoved)
            Log.e("SQL_REMOVE_ERROR", "SQL was not deleted")

    }


    //updates TaskInfo in RV and calls datahelper to update TaskInfo in sql
    //gives error if not updated in sql
    fun editItem(taskObj: TaskInfo, position: Int) {
        if(position == -1){
            Log.e("ADAPTER_POSITION_ERROR", "Position of list out of bounds")


        }
        else {
            mutableList[position] = taskObj
            notifyItemChanged(position)
            val data = DataHelper(context)
            val isNotChanged = data.editTask(taskObj)


            if(isNotChanged)
                Log.e("SQL_EDIT_ERROR", "SQL was not changed")
        }

    }
}







