package com.example.expenselogger

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                                    text = expense.date,
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
                text = "← Swipe Left to Delete",
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