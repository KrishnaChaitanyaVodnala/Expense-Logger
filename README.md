# Expense Logger

This Expense Logger Android Application of mine focuses on storing the regular notes of the daily expenses made by any person. This app sorts the expenses based on the date on which that specific Expense has been enrolled.

## Screenshots

<table>
    <tr>
        <td align = "center">
            <b>Home Screen<b><br>
            <img src = "Expense Logger (Home Screen).jpg" alt = "Home Screen" width="150">
        </td>
        <td>
            <b>Add Expense Screen<b><br>
            <img src = "Expense Logger (Add Screen).jpg" alt = "Home Screen" width="150">
        </td>
        <td>
            <b>View Expense Screen<b><br>
            <img src = "Expense Logger (View Expense).jpg" alt = "Home Screen" width="150">
        </td>
        <td>
            <b>Screen in Dark Mode<b><br>
            <img src = "Expense Logger (Dark Mode).jpg" alt = "Home Screen" width="150">
        </td>
    </tr>
</table>

## Tech stack

- Kotlin
- Jetpack Compose
- MVVM Architecture
- Room Database
- Flow/StateFlow
- Navigation Compose
- Modal Date Picker Dialog
- Material3

## Architecture

**UI → ViewModel → Repository → Room**

- **UI(MainActivity.kt)** Contains all the UI components used to display the information and take the information from the User.

- **ViewModel(ExpensesViewModel.kt)**
    - Manages UI state using `StateFlow`
    - Calls the appropriate Repository functions
    - Validates user input before saving an expense, like
        - Empty fields
        - Negative amounts
        - unselected dates

- **Repository(ExpenseRepository.kt & ExpensesRepositoryImpl.kt)** 
    - **Repository Interface:** Defines the contract for data operations between the Application and data layer.
    - **Repository Impl:** Implements the interface and contains the actual logic for performing those data operations.


- **Room(Expense.kt & ExpensesDao.kt)** Defines the CRUD operation in the Room DB that are to be used in the Application

## Features
- Add daily expenses
- Includes Amount, Date, and detail along side the title
- View the details of the existing Expenses and also can edit those expenses in the view page
- Delete the Expenses using the SwipeToDismissBox

## Setup instructions

You can install the lates APK directly on an Android device without opening Android 