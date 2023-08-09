package com.example.secretnote

import android.annotation.SuppressLint
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID


@Suppress("NAME_SHADOWING")
class ClientSecretNoteService : IntentService("ClientSecretNoteService") {
    val CHANNEL_ID = "CHANNEL_ID"
    private fun createNotificationChannel(CHANNEL_ID:String) {
        val serviceChannel = NotificationChannel(CHANNEL_ID, CHANNEL_ID,
            NotificationManager.IMPORTANCE_DEFAULT)

        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }
    @SuppressLint("MissingPermission")
    override fun onHandleIntent(intent: Intent?) {

        var grpc_connect: grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);



        //  var countchats = 0;
        while(true)
        {
            var er = grpc_connect.check_chat_list()
            if (er>-1)
            {

                break;
            }
            Thread.sleep(50)
        }
        while(true) {
            if (VM_REGg.login.value!=null)
            {
                val nolll:Long = 0;
                Log.d("precheck",  VM_REGg.lastidmsgFORchats.value!!.toString() + " " +  VM_REGg.lastidmsgFORgroupchats.value!!.toString())
                if (grpc_connect.check_new_msg(VM_REGg.login.value!!, VM_REGg.password.value!!, VM_REGg.lastidmsgFORchats.value!!, VM_REGg.lastidmsgFORgroupchats.value!!) > 0.toLong())
                {

                    grpc_connect.check_chat_list()
                    Log.d("precheck",  VM_REGg.chatList.value!!.toString() )
                    if(VM_REGg.chatList.value!!.get(0).dont_read_msg>0)
                    {


                        createNotificationChannel(CHANNEL_ID)
                        val notificationIntent = Intent(this, MainActivity::class.java)
                        val pendingIntent = PendingIntent.getActivity(
                            this,
                            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
                        )
                        val groupKey = "group_msg"
                        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle("У вас новое сообщение!")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentIntent(pendingIntent)
                            .setGroup(groupKey)
                            .build()
                        startForeground(1, notification)
                    }

                }
            }
            Thread.sleep(50)
        }

    }


    override fun onCreate() {

        Log.d("fddf", "onCreate")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.d("fddf", "onDestroy")
        super.onDestroy()
    }

}