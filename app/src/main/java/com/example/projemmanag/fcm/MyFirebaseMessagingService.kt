package com.example.projemmanag.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.projemmanag.R
import com.example.projemmanag.activities.MainActivity
import com.example.projemmanag.activities.SignInActivity
import com.example.projemmanag.firebase.FirestoreClass
import com.example.projemmanag.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG,"Message data payload: ${remoteMessage.data}")
            val title=remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message=remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!
            sendNotification(title,message)

        }
        remoteMessage.notification?.let {
            Log.d(TAG,"Message data payload: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)

    }
    private fun sendRegistrationToServer(token: String?){

    }
    private fun sendNotification(title:String,message:String){
        val intent= if (FirestoreClass().getCurrentUserId().isNotEmpty()){
            Intent(this,MainActivity::class.java)
        }else{
            Intent(this,SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        val channelId=this.resources.getString(R.string.default_notification_channel_id)
        val defaltSoundUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder=NotificationCompat.Builder(
            this,channelId
        ).setSmallIcon(R.drawable.ic_baseline_android_24).setContentTitle(title)
            .setContentText(message).setAutoCancel(true).setSound(defaltSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager=getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            val channel=NotificationChannel(channelId,"Channel Progemmang title",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0,notificationBuilder.build())
    }
    companion object{
        const val TAG="MyFirebaseMsgService"
    }
}