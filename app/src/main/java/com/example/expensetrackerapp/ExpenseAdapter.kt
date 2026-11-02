package com.example.expensetrackerapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.data.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter(
    private var expenseList: List<Expense> = emptyList(),
    private val onItemClickListener: ((Expense) -> Unit)? = null
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvExpenseTitle)
        val tvCategory: TextView = itemView.findViewById(R.id.tvExpenseCategory)
        val tvAmount: TextView = itemView.findViewById(R.id.tvExpenseAmount)
        val tvDate: TextView = itemView.findViewById(R.id.tvExpenseDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]

        holder.tvTitle.text = expense.title
        holder.tvCategory.text = expense.category
        holder.tvAmount.text = String.format(Locale.getDefault(), "₹%.2f", expense.amount)

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        holder.tvDate.text = dateFormat.format(Date(expense.date))

        holder.itemView.setOnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener.invoke(expense)
            } else {
                launchAddExpenseActivity(holder.itemView.context, expense)
            }
        }
    }

    override fun getItemCount(): Int = expenseList.size

    fun setExpenses(expenses: List<Expense>) {
        this.expenseList = expenses
        notifyDataSetChanged()
    }

    private fun launchAddExpenseActivity(context: Context, expense: Expense) {
        val intent = Intent(context, AddExpenseActivity::class.java).apply {
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_ID, expense.id)
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_TITLE, expense.title)
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_AMOUNT, expense.amount)
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_CATEGORY, expense.category)
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_DATE, expense.date)
        }
        context.startActivity(intent)
    }
}

