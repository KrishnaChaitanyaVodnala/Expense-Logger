package com.example.expenselogger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expenselogger.repository.ExpensesRepository

class ExpensesViewModelFactory(val repository: ExpensesRepository): ViewModelProvider.Factory {

    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return ExpensesViewModel(repository) as T
    }
}