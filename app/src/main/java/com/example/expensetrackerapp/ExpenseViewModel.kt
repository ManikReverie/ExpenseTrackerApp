package com.example.expensetrackerapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerapp.data.AppDatabase
import com.example.expensetrackerapp.data.Expense
import com.example.expensetrackerapp.data.ExpenseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseDao: ExpenseDao = AppDatabase.getDatabase(application).expenseDao()
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    fun insert(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        expenseDao.insert(expense)
    }

    fun update(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        expenseDao.update(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        expenseDao.delete(expense)
    }

    suspend fun getExpenseById(id: Int): Expense? = withContext(Dispatchers.IO) {
        expenseDao.getExpenseById(id)
    }
}

