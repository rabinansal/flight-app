package com.travelrights.firebase.service


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.travelrights.activity.MainActivity
import com.travelrights.firebase.app.Config
import com.travelrights.firebase.util.NotificationUtils
import org.json.JSONException
import org.json.JSONObject


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var notificationUtils: NotificationUtils? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.from!!)

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.notification!!.body!!)
            handleNotification(remoteMessage.notification!!.body,remoteMessage.notification!!.title,remoteMessage.notification!!.eventTime,remoteMessage.notification!!.imageUrl?.toString())
        }

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.e(TAG, "IntroDataArray Payload: " + remoteMessage.data.toString())

            try {
                val json = JSONObject(remoteMessage.data.toString())
                handleDataMessage(json)
            } catch (e: Exception) {
                Log.e(TAG, "Exception: " + e.message)
            }

        }
    }

    private fun handleNotification(message: String?,title: String?,timestamp: Long?, imageUrl: String?) {
        if (!NotificationUtils.isAppIsInBackground(applicationContext)) {
            // app is in foreground, broadcast the push message
            val pushNotification = Intent(Config.PUSH_NOTIFICATION)
            pushNotification.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
            val resultIntent = Intent(applicationContext, MainActivity::class.java)
            resultIntent.putExtra("message", message)
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // play notification sound
            val notificationUtils = NotificationUtils(applicationContext)
            notificationUtils.playNotificationSound()

            // check for image attachment
            Log.e(TAG, "Notification Body********:" +imageUrl)
            if (imageUrl==null) {
                showNotificationMessage(applicationContext, title!!, message!!, timestamp.toString(), resultIntent)
            } else {
                // image is present, show notification with image
                showNotificationMessageWithBigImage(applicationContext, title!!, message!!, timestamp.toString(), resultIntent, imageUrl)
            }

        } else {
            // If the app is in background, firebase itself handles the notification
            val notificationUtils = NotificationUtils(applicationContext)
            notificationUtils.playNotificationSound()

        }
    }
    private fun showNotificationMessage(context: Context, title: String, message: String, timeStamp: String, intent: Intent) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils!!.showNotificationMessage(title, message, timeStamp, intent)
    }
    private fun showNotificationMessageWithBigImage(context: Context, title: String, message: String, timeStamp: String, intent: Intent, imageUrl: String) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils!!.showNotificationMessage(title, message, timeStamp, intent, imageUrl)
    }
    private fun handleDataMessage(json: JSONObject) {
        Log.e(TAG, "push json: $json")

        try {
            val data = json.getJSONObject("data")


            if (!NotificationUtils.isAppIsInBackground(applicationContext)) {


            } else {


            }
        } catch (e: JSONException) {
            Log.e(TAG, "Json Exception: " + e.message)
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }

    }

    companion object {

        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}
