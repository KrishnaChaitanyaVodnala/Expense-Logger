package com.example.expenselogger.repository

import com.example.expenselogger.data.Expense
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {
    val expenses: Flow<List<Expense>>

    suspend fun addExpense(expense: Expense)

    suspend fun updateExpense(expense: Expense)

    suspend fun deleteExpense(expense: Expense)
}