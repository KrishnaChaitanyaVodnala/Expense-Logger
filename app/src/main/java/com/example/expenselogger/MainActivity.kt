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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditingScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel,
    addScreen: Boolean = true,
    expense: Expense? = null,
    onAdd: () -> Unit = { }
) {
    var title by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var detail by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf("DD/MM/YYYY") }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    var isEditing by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(expense) {
        if (!addScreen && expense != null) {
            title = expense.title
            amount = expense.amount.toString()
            detail = expense.detail
            selectedDate = expense.date
        }
    }

    var isInputValid by rememberSaveable { mutableStateOf(true) }
    var message by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        OutlinedTextField(
            value = title,
            onValueChange = {
                isEditing = true
                title = it
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text("Enter Title")
            },
            singleLine = true,
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    isEditing = true
                    amount = it
                },
                modifier = Modifier.weight(1f),
                label = { Text("Amount") },
                placeholder = { Text("Rupees(₹)") },
                singleLine = true,
                trailingIcon = {
                  Text("₹")
                },
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Box(
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {
                        isEditing = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Date")
                    },
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if(selectedDate == "DD/MM/YYYY") {
                                    selectedDate = convertMillisToDate(System.currentTimeMillis())
                                }
                                showDatePicker = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date"
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = detail,
            onValueChange = {
                isEditing = true
                detail = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text("Details...")
            },
            textStyle = TextStyle(
                fontSize = 18.sp,
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!isInputValid) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        if(!addScreen){
            if (isEditing) {
                Text(
                    "Changes not saved ❗",
                    color = MaterialTheme.colorScheme.tertiary
                )
            } else {
                Text(
                    "All changes are up to date ✔\uFE0F",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        ElevatedButton(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            ),
            onClick = {
                val amountChecked = amount.toDoubleOrNull()

                if (amountChecked == null) {

                    isInputValid = false
                    message = "Please enter a valid amount!"

                } else {

                    if(addScreen) {

                        val (success, info) = viewModel.addExpense(
                            title = title,
                            amount = amountChecked,
                            date = selectedDate,
                            detail = detail
                        )

                        if (success) {
                            onAdd()
                        }
                        isInputValid = success
                        message = info
                    } else {
                        val (success, info) = viewModel.updateExpense(
                            expense = expense,
                            list = listOf(title, amountChecked, selectedDate, detail)
                        )
                        if(success) {
                            isEditing = false
                        }

                        isInputValid = success
                        message = info
                    }

                }
            }
        ) {
            Text(
                text = if (addScreen) {
                    "Add Expense"
                } else {
                    "Save Changes"
                }
            )
        }

        Spacer(modifier = Modifier.height(5.dp))
    }

    if(showDatePicker) {
        Popup(
            onDismissRequest = { showDatePicker = false},
            alignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                DatePickerModal(
                    datePickerState = datePickerState,
                    onDateSelected = {
                        selectedDate = it
                    },
                    onDismiss = {
                        showDatePicker = false
                    }
                )
            }
        }
    }

}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    datePickerState: DatePickerState  = rememberDatePickerState(),
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    onDateSelected(convertMillisToDate(it))
                }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel,
    onAddExpense: () -> Unit,
    onDisplayExpense: (Int) -> Unit
) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Expenses",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center)
                )

            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            if(expenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No Expenses Added!",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(
                        items = expenses,
                        key = { expense -> expense.id }
                    ) { expense ->
                        val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

                        LaunchedEffect(swipeToDismissBoxState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                            if(swipeToDismissBoxState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.deleteExpense(expense)
                            }
                        }

                        SwipeToDismissBox(
                            state = swipeToDismissBoxState,
                            modifier = Modifier.fillMaxWidth(),
                            backgroundContent = {
                                if(swipeToDismissBoxState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                    Icon(
                                        painter = painterResource(R.drawable.delete_icon),
                                        contentDescription = "Remove Expense",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .wrapContentSize(Alignment.CenterEnd)
                                            .padding(12.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            enableDismissFromStartToEnd = false
                        ) {

                            Row(
                                modifier = Modifier
                                    .clickable {
                                        onDisplayExpense(expense.id)
                                    }
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(vertical = 10.dp)
                                    .background(MaterialTheme.colorScheme.surface),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = expense.title,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "← Swipe to Delete",
                                    fontWeight = FontWeight.Light,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier
                                        .fillMaxHeight(),
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                            }

                        }
                    }
                }
            }

            Text(
                text = "← Swipe to Delete",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            ElevatedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAddExpense,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text("Add Expense")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
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