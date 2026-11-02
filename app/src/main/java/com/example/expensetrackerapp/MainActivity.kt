package com.example.expensetrackerapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.data.Expense
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter

    private lateinit var tvTotalExpense: TextView
    private lateinit var tvExpenseCount: TextView
    private lateinit var tvEmptyState: TextView
    private lateinit var recyclerViewExpenses: RecyclerView
    private lateinit var fabAddExpense: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupViewModel()

        fabAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViews() {
        tvTotalExpense = findViewById(R.id.tvTotalExpense)
        tvExpenseCount = findViewById(R.id.tvExpenseCount)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        recyclerViewExpenses = findViewById(R.id.recyclerViewExpenses)
        fabAddExpense = findViewById(R.id.fabAddExpense)
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter { expense ->
            openEditExpense(expense)
        }
        recyclerViewExpenses.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = expenseAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupViewModel() {
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        expenseViewModel.allExpenses.observe(this) { expenses ->
            updateExpenseList(expenses)
            calculateAndUpdateTotal(expenses)
        }
    }

    private fun updateExpenseList(expenses: List<Expense>) {
        expenseAdapter.setExpenses(expenses)
        if (expenses.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            recyclerViewExpenses.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            recyclerViewExpenses.visibility = View.VISIBLE
        }
    }

    private fun calculateAndUpdateTotal(expenses: List<Expense>) {
        val total = expenses.sumOf { it.amount }
        tvTotalExpense.text = String.format(Locale.getDefault(), "₹%.2f", total)

        val countText = if (expenses.size == 1) "1 transaction" else "${expenses.size} transactions"
        tvExpenseCount.text = countText
    }

    private fun openEditExpense(expense: Expense) {
        val intent = Intent(this, AddExpenseActivity::class.java).apply {
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_ID, expense.id)
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_TITLE, expense.title)
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_AMOUNT, expense.amount)
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_CATEGORY, expense.category)
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_DATE, expense.date)
        }
        startActivity(intent)
    }
}
