package com.example.expensetrackerapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.expensetrackerapp.data.Expense
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AddExpenseActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EXPENSE_ID = "EXTRA_EXPENSE_ID"
        const val EXTRA_EXPENSE_TITLE = "EXTRA_EXPENSE_TITLE"
        const val EXTRA_EXPENSE_AMOUNT = "EXTRA_EXPENSE_AMOUNT"
        const val EXTRA_EXPENSE_CATEGORY = "EXTRA_EXPENSE_CATEGORY"
        const val EXTRA_EXPENSE_DATE = "EXTRA_EXPENSE_DATE"
    }

    private lateinit var expenseViewModel: ExpenseViewModel

    private lateinit var btnBack: ImageButton
    private lateinit var tvHeaderTitle: TextView
    private lateinit var tilExpenseTitle: TextInputLayout
    private lateinit var etExpenseTitle: TextInputEditText
    private lateinit var tilExpenseAmount: TextInputLayout
    private lateinit var etExpenseAmount: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSaveExpense: MaterialButton
    private lateinit var btnDeleteExpense: MaterialButton

    private var expenseId: Int = -1
    private var expenseDate: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainAddExpense)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupViewModel()
        setupCategorySpinner()
        handleIntentData()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)
        tilExpenseTitle = findViewById(R.id.tilExpenseTitle)
        etExpenseTitle = findViewById(R.id.etExpenseTitle)
        tilExpenseAmount = findViewById(R.id.tilExpenseAmount)
        etExpenseAmount = findViewById(R.id.etExpenseAmount)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSaveExpense = findViewById(R.id.btnSaveExpense)
        btnDeleteExpense = findViewById(R.id.btnDeleteExpense)
    }

    private fun setupViewModel() {
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
    }

    private fun setupCategorySpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.expense_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }
    }

    private fun handleIntentData() {
        expenseId = intent.getIntExtra(EXTRA_EXPENSE_ID, -1)

        if (expenseId != -1) {
            // Edit Mode
            tvHeaderTitle.setText(R.string.edit_expense_title)
            btnSaveExpense.setText(R.string.update_expense)
            btnDeleteExpense.visibility = View.VISIBLE

            val titleExtra = intent.getStringExtra(EXTRA_EXPENSE_TITLE)
            val amountExtra = intent.getDoubleExtra(EXTRA_EXPENSE_AMOUNT, -1.0)
            val categoryExtra = intent.getStringExtra(EXTRA_EXPENSE_CATEGORY)
            expenseDate = intent.getLongExtra(EXTRA_EXPENSE_DATE, System.currentTimeMillis())

            if (titleExtra != null && amountExtra >= 0) {
                etExpenseTitle.setText(titleExtra)
                etExpenseAmount.setText(String.format(java.util.Locale.US, "%.2f", amountExtra))
                selectCategoryInSpinner(categoryExtra)
            } else {
                // Fetch from DB if extras are missing
                lifecycleScope.launch {
                    val expense = expenseViewModel.getExpenseById(expenseId)
                    expense?.let {
                        etExpenseTitle.setText(it.title)
                        etExpenseAmount.setText(String.format(java.util.Locale.US, "%.2f", it.amount))
                        selectCategoryInSpinner(it.category)
                        expenseDate = it.date
                    }
                }
            }
        } else {
            // Add Mode
            tvHeaderTitle.setText(R.string.add_expense_title)
            btnSaveExpense.setText(R.string.save_expense)
            btnDeleteExpense.visibility = View.GONE
        }
    }

    private fun selectCategoryInSpinner(category: String?) {
        if (category == null) return
        val adapter = spinnerCategory.adapter ?: return
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString().equals(category, ignoreCase = true)) {
                spinnerCategory.setSelection(i)
                break
            }
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSaveExpense.setOnClickListener {
            saveExpense()
        }

        btnDeleteExpense.setOnClickListener {
            deleteExpense()
        }
    }

    private fun saveExpense() {
        val title = etExpenseTitle.text?.toString()?.trim() ?: ""
        val amountStr = etExpenseAmount.text?.toString()?.trim() ?: ""
        val category = spinnerCategory.selectedItem?.toString() ?: "Other"

        var isValid = true

        if (title.isEmpty()) {
            tilExpenseTitle.error = getString(R.string.error_empty_title)
            isValid = false
        } else {
            tilExpenseTitle.error = null
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            tilExpenseAmount.error = getString(R.string.error_invalid_amount)
            isValid = false
        } else {
            tilExpenseAmount.error = null
        }

        if (!isValid) return

        if (expenseId != -1) {
            // Update existing expense
            val updatedExpense = Expense(
                id = expenseId,
                title = title,
                amount = amount!!,
                category = category,
                date = expenseDate
            )
            expenseViewModel.update(updatedExpense)
            Toast.makeText(this, R.string.expense_updated, Toast.LENGTH_SHORT).show()
        } else {
            // Insert new expense
            val newExpense = Expense(
                title = title,
                amount = amount!!,
                category = category,
                date = System.currentTimeMillis()
            )
            expenseViewModel.insert(newExpense)
            Toast.makeText(this, R.string.expense_saved, Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun deleteExpense() {
        if (expenseId != -1) {
            val title = etExpenseTitle.text?.toString()?.trim() ?: ""
            val amount = etExpenseAmount.text?.toString()?.toDoubleOrNull() ?: 0.0
            val category = spinnerCategory.selectedItem?.toString() ?: "Other"

            val expenseToDelete = Expense(
                id = expenseId,
                title = title,
                amount = amount,
                category = category,
                date = expenseDate
            )
            expenseViewModel.delete(expenseToDelete)
            Toast.makeText(this, R.string.expense_deleted, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

