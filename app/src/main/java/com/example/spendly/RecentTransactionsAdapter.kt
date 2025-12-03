package com.example.spendly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecentTransactionsAdapter(
    private val transactions: List<String>
) : RecyclerView.Adapter<RecentTransactionsAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)

        fun bind(transaction: String) {
            val (title, amount, category, _, type) = transaction.split("|")

            tvTitle.text = title
            tvAmount.text = "Rs.${amount}"
            tvCategory.text = category

            val (iconRes, colorRes) = when(type) {
                "Income" -> Pair(R.drawable.ic_income, R.color.income_green)
                else -> Pair(R.drawable.ic_expense, R.color.expense_red)
            }

            ivIcon.setImageResource(iconRes)
            ivIcon.setColorFilter(itemView.context.getColor(colorRes))
            tvAmount.setTextColor(itemView.context.getColor(colorRes))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount() = transactions.size
}