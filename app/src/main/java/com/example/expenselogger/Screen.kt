package com.example.expenselogger

sealed class Screen(val route: String) {
    object HomeScreen: Screen(route = "home_screen")
    object AddExpenseScreen: Screen(route = "add_expense_screen")
    object DisplayExpenseScreen: Screen(route = "display_expense_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}