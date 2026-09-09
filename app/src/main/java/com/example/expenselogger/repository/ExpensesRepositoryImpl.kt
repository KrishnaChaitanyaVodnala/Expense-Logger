package com.example.expenselogger.repository

import com.example.expenselogger.data.Expense
import com.example.expenselogger.data.ExpensesDao
import kotlinx.coroutines.flow.Flow

class ExpensesRepositoryImpl(val expensesDao: ExpensesDao) : ExpensesRepository {
    override val expenses: Flow<List<Expense>> = expensesDao.getAll()

    override suspend fun addExpense(expense: Expense) {
        expensesDao.insert(expense)
    }

    override suspend fun updateExpense(expense: Expense) {
        expensesDao.update(expense)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expensesDao.delete(expense)
    }
}