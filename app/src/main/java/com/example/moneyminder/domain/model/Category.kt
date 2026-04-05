package com.example.moneyminder.domain.model

import androidx.compose.ui.graphics.Color

enum class Category(val title: String, val color: Color) {
    // Expense Categories
    FOOD("Food", Color(0xFFF44336)),
    TRAVEL("Travel", Color(0xFF2196F3)),
    BILLS("Bills", Color(0xFFFF9800)),
    SHOPPING("Shopping", Color(0xFFE91E63)),
    ENTERTAINMENT("Entertainment", Color(0xFF9C27B0)),
    HEALTH("Health", Color(0xFF4CAF50)),
    EDUCATION("Education", Color(0xFF3F51B5)),
    RENT("Rent", Color(0xFF795548)),
    
    // Income Categories
    SALARY("Salary", Color(0xFF4CAF50)),
    GIFT("Gift", Color(0xFFFFEB3B)),
    INVESTMENT("Investment", Color(0xFF00BCD4)),

    OTHERS("Others", Color(0xFF607D8B))
}
