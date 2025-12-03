package com.example.spendly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class NotificationAdapter(
    private val notifications: List<String>,
    private val onRemove: (String) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNotification: TextView = itemView.findViewById(R.id.tvNotification)
        private val btnRemove: MaterialButton = itemView.findViewById(R.id.btnRemove)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)

        fun bind(notification: String, onRemove: (String) -> Unit) {
            tvNotification.text = notification
            btnRemove.setOnClickListener { onRemove(notification) }

            // Set icon and color based on notification type
            when {
                notification.contains("Income") -> {
                    ivIcon.setImageResource(R.drawable.ic_income)
                    ivIcon.setColorFilter(
                        itemView.context.getColor(R.color.income_green)
                    )
                }
                notification.contains("Expense") -> {
                    ivIcon.setImageResource(R.drawable.ic_expense)
                    ivIcon.setColorFilter(
                        itemView.context.getColor(R.color.expense_red)
                    )
                }
                notification.contains("Budget") -> {
                    ivIcon.setImageResource(R.drawable.ic_alert)
                    ivIcon.setColorFilter(
                        itemView.context.getColor(R.color.warning_orange)
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position], onRemove)
    }

    override fun getItemCount() = notifications.size
}