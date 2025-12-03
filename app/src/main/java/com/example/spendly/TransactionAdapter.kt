package com.example.spendly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class TransactionAdapter(
    private val transactions: List<String>,
    private val onEditTransaction: (String) -> Unit,
    private val onDeleteTransaction: (String) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction, onEditTransaction, onDeleteTransaction)
    }

    override fun getItemCount() = transactions.size

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTransactionDetails: TextView = itemView.findViewById(R.id.tvTransactionDetails)
        private val tvTransactionDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        private val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)

        fun bind(transaction: String, onEditTransaction: (String) -> Unit, onDeleteTransaction: (String) -> Unit) {
            val parts = transaction.split("|")
            val title = parts[0]
            val amount = parts[1]
            val category = parts[2]
            val date = parts[3]
            val type = parts[4]

            // Set transaction details with color based on type
            val context = itemView.context
            val textColor = when (type) {
                "Income" -> context.getColor(R.color.income_green)
                else -> context.getColor(R.color.expense_red)
            }

            tvTransactionDetails.setTextColor(textColor)
            tvTransactionDetails.text = "$title - $category\nRs.$amount"
            tvTransactionDate.text = date

            btnEdit.setOnClickListener { onEditTransaction(transaction) }
            btnDelete.setOnClickListener { onDeleteTransaction(transaction) }
        }
    }
}