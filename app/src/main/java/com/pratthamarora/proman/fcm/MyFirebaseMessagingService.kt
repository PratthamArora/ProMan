package com.pratthamarora.proman.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pratthamarora.proman.R
import com.pratthamarora.proman.firebase.FirestoreClass
import com.pratthamarora.proman.ui.activities.MainActivity
import com.pratthamarora.proman.ui.activities.SignInActivity
import com.pratthamarora.proman.utils.Constants


class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            // The notification data payload is printed in the log.
            Log.i(TAG, "Message data payload: " + remoteMessage.data)

            // The Title and Message are assigned to the local variables
            val title = remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message = remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!

            // Finally sent them to build a notification.
            sendNotification(title, message)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.e(TAG, "Refreshed token: $token")

        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // Here we have saved the token in the Shared Preferences
        val sharedPreferences =
            this.getSharedPreferences(Constants.PROMAN_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.FCM_TOKEN, token)
        editor.apply()
    }


    private fun sendNotification(title: String, message: String) {

        val intent: Intent = if (FirestoreClass().getCurrentUserID().isNotEmpty()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, SignInActivity::class.java)
        }
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel ProMan title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}