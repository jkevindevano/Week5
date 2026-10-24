package com.example.week5.viewmodel

import androidx.lifecycle.ViewModel
import com.example.week5.data.DataExpense
import com.example.week5.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExpenseViewModel : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(DataExpense.initialExpenses)
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val totalExpense: Double
        get() = _expenses.value.sumOf { it.amount }

    fun addExpense(name: String, amountText: String) {
        val amount = amountText.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _errorMessage.value = "Amount must be greater than 0"
            return
        }

        _errorMessage.value = null
        val newList = _expenses.value.toMutableList()
        newList.add(Expense(name = name.ifBlank { "Expense" }, amount = amount))
        _expenses.value = newList
    }

    fun deleteExpense(expense: Expense) {
        val newList = _expenses.value.toMutableList()
        newList.remove(expense)
        _expenses.value = newList
    }

    fun updateExpenseAmount(expenseId: String, newAmount: Double) {
        if (newAmount <= 0) return
        _expenses.value = _expenses.value.map {
            if (it.id == expenseId) it.copy(amount = newAmount) else it
        }
    }
}