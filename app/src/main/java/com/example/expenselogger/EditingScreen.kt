package com.example.expenselogger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.expenselogger.data.Expense
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val endOfToday = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis
                return utcTimeMillis <= endOfToday
            }
        }
    )

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
                        isEditing = true
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