/*
 * Copyright (c) 2022.
 * @author MriDx
 */

package tech.sumato.utility360.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tech.sumato.utility360.R

class FCMService : FirebaseMessagingService() {

    private val notificationChannelId: String = "UTILITY-360-NOTIFICATION-CHANNEL-ID"
    private val notificationChannelName: String = "UTILITY-360-NOTIFICATION-CHANNEL"

    private fun createVibration(): LongArray? {
        return longArrayOf(0, 1000, 0)
    }

    private fun defaultSoundUri(): Uri? {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            setupChannel(notificationManager)

        notificationManager.notify(1000000, createNotification(p0))

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChannel(notificationManager: NotificationManagerCompat) {
        val adminChannel = NotificationChannel(
            notificationChannelId,
            notificationChannelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        adminChannel.description = notificationChannelName
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager.createNotificationChannel(adminChannel)
    }

    private fun createNotification(message: RemoteMessage): Notification {
        Log.d("mridx", "createNotification: ${message.data}")
        Log.d("mridx", "createNotification: ${message.notification?.imageUrl}")
        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        notificationBuilder
            .setSmallIcon(R.drawable.ic_stat_utlity_logo)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setAutoCancel(true)
            .setVibrate(createVibration())
            .setLights(Color.RED, 1, 1)
            .setSound(defaultSoundUri())
        val imageUrl: String? = message.notification?.imageUrl?.toString() ?: message.data["image"]
        if (imageUrl != null) {
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(getBitmap(imageUrl = imageUrl))
            )
        } else {
            notificationBuilder.setStyle(
                NotificationCompat.BigTextStyle().bigText(message.notification?.body)
            )
        }
        //.setStyle(NotificationCompat.BigTextStyle().bigText(message.notification?.body))
        /*.setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(getBitmap(imageUrl = message.data["image"]))
        )*/
        //.setContentIntent(createIntent(message))
        return notificationBuilder.build()
    }

    private fun getBitmap(imageUrl: String?): Bitmap? {
        imageUrl ?: return null
        return Glide.with(this).asBitmap().load(imageUrl).submit().get()
    }


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        //submit token to server
        Log.d("mridx", "onNewToken: new token generated, submitting to server")
        //UserRepository.submitDeviceToken(deviceToken = p0)
    }

}