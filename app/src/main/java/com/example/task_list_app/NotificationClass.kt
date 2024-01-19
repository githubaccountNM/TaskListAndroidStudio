
package com.example.task_list_app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

class NotificationClass : BroadcastReceiver() {

    private val CHANNEL_ID = "Task_List.apps.notification"
    private val TASK_TITLE = "TASK_TITLE"
    private val NOTIFICATION_REQUEST_CODE = 123


    //for API 26-30, Did not implement notification permission necessary for API 33
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context? , intent: Intent?) {

        //channel (necessary for api 26)
        createNotifChannel(context)

        //customize notifications
        var builder: NotificationCompat.Builder? = null
        val message = intent?.getStringExtra(TASK_TITLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder = context?.let {
                NotificationCompat.Builder(it , CHANNEL_ID).apply {
                    setContentTitle(message)
                    setSmallIcon(android.R.drawable.ic_dialog_info)
                }
            }


        } else {
            builder = context?.let {
                NotificationCompat.Builder(it).apply {
                    setContentTitle(message)
                    setSmallIcon(android.R.drawable.ic_dialog_info)
                }
            }
        }


        //build notifications
        val notifManager = context?.let { NotificationManagerCompat.from(it) }
        builder?.build()?.let { notifManager?.notify(NOTIFICATION_REQUEST_CODE, it) }

    }


    //channel to use notification, calls new intent to refresh onReceive
   fun createNotifChannel(context: Context?){

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID ,
                "NOTIF DETAIL" ,
                NotificationManager.IMPORTANCE_HIGH
            )

            val notifManager = context?.getSystemService(NotificationManager::class.java)
            notifManager?.createNotificationChannel(channel)

        }

    }


    //to schedule at certain date/time
    //context, class TaskInfo, SQL id of TaskInfo
    //supressed bc canScheduleExactAlarms() can only be accessed by api 33
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotif(context: Context? , taskObj: TaskInfo){
        //call for onReceive
        val intent = Intent(context , NotificationClass::class.java)
        intent.putExtra("TASK_TITLE", taskObj.getTask())
        val pendingNotif =
            taskObj.getId()?.let {
                PendingIntent.getBroadcast(context ,
                    it , intent , PendingIntent.FLAG_UPDATE_CURRENT)
            }

        //set date and time for alarm manager
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(taskObj.getYear(),taskObj.getMonth() - 1,taskObj.getDay(),taskObj.getHour(),taskObj.getMin())

        }

        //sets alarm manager
        val manager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (pendingNotif != null) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP , calendar.timeInMillis , pendingNotif)
        }
    }


    //to cancel at certain date/time
    //context, class TaskInfo
    fun cancelNotif(context: Context?, taskObj: TaskInfo) {
        //calls for notification with same title
        val intent = Intent(context , NotificationClass::class.java)
        intent.putExtra("TASK_TITLE" , taskObj.getTask())

        val pendingNotif = taskObj.getId()?.let {
            PendingIntent.getBroadcast(
                context ,
                it ,
                intent ,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        //cancels alarm manager
        val manager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (pendingNotif != null) {
            manager.cancel(pendingNotif)
        }
    }


}



