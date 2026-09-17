package com.example.expenselogger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenselogger.data.Expense
import com.example.expenselogger.repository.ExpensesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ExpensesViewModel(val repository: ExpensesRepository): ViewModel() {
    val expenses: StateFlow<List<Expense>> = repository.expenses
        .map { list -> list.sortedByDescending { parseDateToMillis(it.date) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private fun parseDateToMillis(dateStr: String): Long {
        return try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr)?.time ?: 0L
        } catch (e: Exception) {
            return 0L
        }
    }

    fun addExpense(title: String, amount: Double, date: String, detail: String = ""): Pair<Boolean, String> {
        if(title.isEmpty()) {
            return Pair(false, "Title field is Empty!")
        } else if(amount <= 0.0) {
            return Pair(false, "Please enter the amount of the Expense")
        } else if(date.isEmpty() || date == "DD/MM/YYYY") {
            return Pair(false, "Please select the date!")
        }

        viewModelScope.launch {
            repository.addExpense(
                Expense(
                    title = title,
                    amount = amount,
                    date = date,
                    detail = detail
                )
            )
        }

        return Pair(true, "Expense added Successfully!")
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun updateExpense(expense: Expense?, list: List<Any>): Pair<Boolean, String> {
        val title = list[0] as String
        val amount = list[1] as Double
        val date = list[2] as String
        val detail = list[3] as String

        if(title.isEmpty()) {
            return Pair(false, "Title field is Empty!")
        } else if(amount <= 0.0) {
            return Pair(false, "Please enter the amount of the Expense")
        } else if(date.isEmpty() || date == "DD/MM/YYYY") {
            return Pair(false, "Please select the date!")
        }

        viewModelScope.launch {

            repository.updateExpense(
                expense!!.copy(
                    title = title,
                    amount = amount,
                    date = date,
                    detail = detail
                )
            )

        }

        return Pair(true, "Successfully updated!")
    }

    fun getDetails(id: Int): Expense? {
        return expenses.value.find {
            it.id == id
        }
    }
}