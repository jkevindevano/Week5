package com.example.week5.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.week5.model.Expense
import com.example.week5.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel = viewModel()) {
    val expenses by viewModel.expenses.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var nameInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }

    var selectedExpenseForEdit by remember { mutableStateOf<Expense?>(null) }
    var editAmountInput by remember { mutableStateOf("") }

    val localeID = Locale("in", "ID")
    val rupiahFormat = NumberFormat.getCurrencyInstance(localeID).apply {
        maximumFractionDigits = 0
    }

    Box () {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "Expenses",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Total Expense : ${rupiahFormat.format(viewModel.totalExpense)}",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Expense Name", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount of Expense", color = Color.Gray) },
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        viewModel.addExpense(nameInput, amountInput)
                        if (amountInput.toDoubleOrNull() ?: 0.0 > 0) {
                            nameInput = ""
                            amountInput = ""
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Expense")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expenses, key = { it.id }) { expense ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Name : ${expense.name}",
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = rupiahFormat.format(expense.amount),
                                    color = Color.Gray
                                )
                            }
                            Row {
                                IconButton(onClick = {
                                    selectedExpenseForEdit = expense
                                    editAmountInput = expense.amount.toInt().toString()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Expense",
                                        tint = Color(0xFFC0CA33)
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteExpense(expense) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Expense",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedExpenseForEdit != null) {
        AlertDialog(
            onDismissRequest = { selectedExpenseForEdit = null },
            title = { Text("Edit Amount") },
            text = {
                OutlinedTextField(
                    value = editAmountInput,
                    onValueChange = { editAmountInput = it },
                    label = { Text("New Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val newAmount = editAmountInput.toDoubleOrNull()
                    if (newAmount != null && newAmount > 0) {
                        viewModel.updateExpenseAmount(selectedExpenseForEdit!!.id, newAmount)
                        selectedExpenseForEdit = null
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedExpenseForEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}