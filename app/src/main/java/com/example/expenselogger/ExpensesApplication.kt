package com.example.expenselogger

import android.app.Application
import com.example.expenselogger.data.ExpensesDatabase
import com.example.expenselogger.repository.ExpensesRepository
import com.example.expenselogger.repository.ExpensesRepositoryImpl
import kotlin.getValue

class ExpensesApplication: Application() {
    val repository: ExpensesRepository by lazy {
        ExpensesRepositoryImpl(ExpensesDatabase.getDatabase(this).expensesDao())
    }
}