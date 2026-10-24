package com.example.week5.model

import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var amount: Double
)