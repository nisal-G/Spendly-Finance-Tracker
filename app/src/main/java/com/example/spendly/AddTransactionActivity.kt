package com.example.spendly

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etTitle: TextInputEditText
    private lateinit var etAmount: TextInputEditText
    private lateinit var categoryAutoComplete: AutoCompleteTextView
    private lateinit var btnAddIncome: MaterialButton
    private lateinit var btnAddExpense: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize views
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        categoryAutoComplete = findViewById(R.id.spinnerCategory)
        btnAddIncome = findViewById(R.id.btnAddIncome)
        btnAddExpense = findViewById(R.id.btnAddExpense)

        val incomeCategories = arrayOf(
            "Salary", "Freelance", "Business", "Investments", "Gifts", "Rental Income", "Other"
        )
        val expenseCategories = arrayOf(
            "Rent/Mortgage", "Utilities", "Groceries", "Transportation", "Insurance", "Loan Payments"
        )

        // Create custom array adapters using the custom dropdown item layout
        val incomeAdapter = ArrayAdapter(this, R.layout.dropdown_item, incomeCategories)
        val expenseAdapter = ArrayAdapter(this, R.layout.dropdown_item, expenseCategories)

        // Set default categories (income)
        categoryAutoComplete.setAdapter(incomeAdapter)

        btnAddIncome.setOnClickListener {
            categoryAutoComplete.setAdapter(incomeAdapter)
            addTransaction(true)
        }

        btnAddExpense.setOnClickListener {
            categoryAutoComplete.setAdapter(expenseAdapter)
            addTransaction(false)
        }

        createNotificationChannel()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_add_transaction

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_add_transaction -> true // Already here
                R.id.nav_view_transactions -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun addTransaction(isIncome: Boolean) {
        val title = etTitle.text?.toString() ?: ""
        val amount = etAmount.text?.toString()?.toFloatOrNull()
        val category = categoryAutoComplete.text.toString()
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        if (title.isNotEmpty() && amount != null && category.isNotEmpty()) {
            val transaction = "$title|$amount|$category|$date|${if (isIncome) "Income" else "Expense"}"
            val transactions = sharedPreferences.getStringSet("transactions", mutableSetOf())!!.toMutableSet()
            transactions.add(transaction)
            sharedPreferences.edit().putStringSet("transactions", transactions).apply()

            val currentBudget = sharedPreferences.getFloat("currentBudget", 0.0f)
            val newBudget = if (isIncome) currentBudget + amount else currentBudget - amount
            sharedPreferences.edit().putFloat("currentBudget", newBudget).apply()

            if (!isIncome) {
                checkBudgetThreshold(newBudget)
            }

            triggerNotification(title, amount, category, date, isIncome)

            Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBudgetThreshold(newBudget: Float) {
        val totalBudget = sharedPreferences.getFloat("totalBudget", 0.0f)
        val threshold = 0.8f * totalBudget

        if (newBudget < threshold) {
            triggerBudgetThresholdNotification()
        }
    }

    private fun triggerNotification(title: String, amount: Float, category: String, date: String, isIncome: Boolean) {
        val notificationTitle = if (isIncome) "New Income Added" else "New Expense Added"
        val notificationContent = "$date: $title - $category - Rs.$amount"

        val builder = NotificationCompat.Builder(this, "transaction_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }

        // Save notification to SharedPreferences
        val notifications = sharedPreferences.getStringSet("notifications", mutableSetOf())!!.toMutableSet()
        notifications.add(notificationContent)
        sharedPreferences.edit().putStringSet("notifications", notifications).apply()
    }

    private fun triggerBudgetThresholdNotification() {
        val notificationTitle = "Budget Alert"
        val notificationContent = "Your expenses are close to exceeding your budget."

        val builder = NotificationCompat.Builder(this, "transaction_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }

        // Save notification to SharedPreferences
        val notifications = sharedPreferences.getStringSet("notifications", mutableSetOf())!!.toMutableSet()
        notifications.add(notificationContent)
        sharedPreferences.edit().putStringSet("notifications", notifications).apply()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Transaction Notifications"
            val descriptionText = "Notifications for new transactions"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("transaction_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}