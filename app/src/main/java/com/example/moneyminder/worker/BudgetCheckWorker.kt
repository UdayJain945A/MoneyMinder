package com.example.moneyminder.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneyminder.domain.repository.ExpenseRepository
import com.example.moneyminder.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.Locale

@HiltWorker
class BudgetCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ExpenseRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val budget = repository.getBudget().first() ?: return Result.success()
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val startOfMonth = calendar.timeInMillis

        val totalSpending = repository.getTotalSpending(startOfMonth, System.currentTimeMillis()).first() ?: 0.0

        if (totalSpending >= budget.monthlyLimit) {
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.showNotification(
                "Budget Exceeded!",
                "You have spent $${String.format(Locale.US, "%.2f", totalSpending)}, which is over your budget of $${String.format(Locale.US, "%.2f", budget.monthlyLimit)}."
            )
        } else if (totalSpending >= budget.monthlyLimit * 0.9)
        {
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.showNotification(
                "Budget Warning",
                "You have used 90% of your monthly budget."
            )
        }
        return Result.success()
    }
}
