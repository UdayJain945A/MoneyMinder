package com.example.moneyminder.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val month: Int, // 1-12
    val year: Int
)
