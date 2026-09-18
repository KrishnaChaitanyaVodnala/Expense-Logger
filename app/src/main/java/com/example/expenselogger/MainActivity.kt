package com.example.expenselogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expenselogger.ui.theme.ExpenseLoggerTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expenselogger.data.Expense
import com.example.expenselogger.repository.ExpensesRepository
import kotlinx.coroutines.flow.Flow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Popup
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.SelectableDates
import java.util.Calendar


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseLoggerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Surface(tonalElevation = 5.dp) {

                        val repository = (application as ExpensesApplication).repository
                        val viewModel: ExpensesViewModel = viewModel(factory = ExpensesViewModelFactory(repository))
                        AppNavHost(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    viewModel: ExpensesViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                modifier = modifier,
                viewModel = viewModel,
                onAddExpense = {
                    navController.navigate(Screen.AddExpenseScreen.route)
                },
                onDisplayExpense = {
                    navController.navigate(Screen.DisplayExpenseScreen.withArgs(it.toString()))
                }
            )
        }

        composable(route = Screen.AddExpenseScreen.route) {

            EditingScreen(
                modifier = modifier,
                viewModel = viewModel,
                addScreen = true,
                onAdd = {
                    navController.popBackStack()
                }
            )

        }

        composable(
            route = Screen.DisplayExpenseScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { entry ->

            val id = entry.arguments?.getInt("id")

            if(id == null) {
                navController.popBackStack()
                return@composable
            }

            val expense = viewModel.getDetails(id)

            EditingScreen(
                modifier = modifier,
                viewModel = viewModel,
                addScreen = false,
                expense = expense
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseLoggerPreview() {
    ExpenseLoggerTheme {
        val repository = object: ExpensesRepository {

            override val expenses: Flow<List<Expense>>
                get() = TODO("Not yet implemented")

            override suspend fun addExpense(expense: Expense) {
                TODO("Not yet implemented")
            }

            override suspend fun updateExpense(expense: Expense) {
                TODO("Not yet implemented")
            }

            override suspend fun deleteExpense(expense: Expense) {
                TODO("Not yet implemented")
            }

        }
        val viewModel: ExpensesViewModel = viewModel(factory = ExpensesViewModelFactory(repository))
        AppNavHost(
            viewModel = viewModel
        )
    }
}