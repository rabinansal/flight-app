package com.travelrights.firebase.app

/**
 * Created by Ravi Tamada on 28/09/16.
 * www.androidhive.info
 */

object Config {

    // global topic to receive app wide push notifications
    const val TOPIC_GLOBAL = "global"

    // broadcast receiver intent filters
    const val REGISTRATION_COMPLETE = "registrationComplete"
    const val PUSH_NOTIFICATION = "pushNotification"

    // id to handle the notification in the notification tray
    const val NOTIFICATION_ID = 100
    const val NOTIFICATION_ID_BIG_IMAGE = 101

    const val SHARED_PREF = "ah_firebase"
}
