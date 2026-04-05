package com.example.moneyminder.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey
    val id: Int = 1, // Only one budget for now
    val monthlyLimit: Double
)
