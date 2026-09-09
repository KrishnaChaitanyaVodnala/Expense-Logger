package com.example.expenselogger.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Expense::class], version = 1, exportSchema = false)
abstract class ExpensesDatabase: RoomDatabase() {
    abstract fun expensesDao(): ExpensesDao

    companion object {
        @Volatile
        private var Instance: ExpensesDatabase? = null

        fun getDatabase(context: Context): ExpensesDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ExpensesDatabase::class.java, "expenses_database")
                    .build()
                    .also {
                        Instance = it
                    }
            }
        }
    }
}