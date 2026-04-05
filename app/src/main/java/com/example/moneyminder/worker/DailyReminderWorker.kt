package com.example.moneyminder.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneyminder.R
import com.example.moneyminder.data.local.preferences.PreferenceManager
import com.example.moneyminder.domain.repository.TransactionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Date

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: TransactionRepository,
    private val preferenceManager: PreferenceManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val notificationsEnabled = preferenceManager.notificationsEnabled.first()
        if (!notificationsEnabled) return Result.success()

        val lastTransaction = repository.getLastTransaction()
        val currentTime = System.currentTimeMillis()
        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000L

        val shouldNotify = if (lastTransaction == null) {
            true
        } else {
            (currentTime - lastTransaction.date) > twentyFourHoursInMillis
        }

        if (shouldNotify) {
            showNotification()
        }

        return Result.success()
    }

    private fun showNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to add transactions if you haven't added one in 24 hours"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use a better icon if available
            .setContentTitle("MoneyMinder Reminder")
            .setContentText("You haven't added any transactions in the last 24 hours. Keep your records up to date!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
