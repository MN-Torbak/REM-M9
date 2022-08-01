package com.openclassrooms.firebaseREM.Notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.openclassrooms.firebaseREM.api.Manager
import java.util.*

class NotificationsWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    var mManager : Manager? = null

    override fun doWork(): Result {
        return Result.success()
    }

    private fun displayNotifications(
        /*TODO: mettre les variables ici:
        restaurantName: String,
        restaurantAddress: String,
        workmateWhoJoined: String */
    ) {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext, "k"
        )
           // .setSmallIcon(/*TODO: mettre un icon*/)
          //  .setContentTitle(/*TODO: mettre un icon*/)
            .setContentText("")
            .setStyle(
                NotificationCompat.BigTextStyle()
              //      .bigText(/*TODO: faire la notification ici (contenu de son texte) >getText(restaurantName, restaurantAddress, workmateWhoJoined)< */)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManager = NotificationManagerCompat.from(
            applicationContext
        )
        notificationManager.notify(0, builder.build())
    }

    init {
        mManager = Manager()
    }
}