package com.example.spendly

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etTitle: TextInputEditText
    private lateinit var etAmount: TextInputEditText
    private lateinit var categoryAutoComplete: AutoCompleteTextView
    private lateinit var btnUpdate: MaterialButton

    private lateinit var btnBack: ImageButton
    private var originalTransaction: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize views
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        categoryAutoComplete = findViewById(R.id.spinnerCategory)
        btnUpdate = findViewById(R.id.btnUpdate)

        btnBack = findViewById(R.id.btnBack)



        // Setup back button
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Setup category dropdown
        val categories = arrayOf(
            "Rent/Mortgage", "Utilities", "Groceries", "Transportation",
            "Insurance", "Loan Payments", "Salary", "Freelance",
            "Business", "Investments", "Gifts", "Rental Income", "Other"
        )
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, categories)
        categoryAutoComplete.setAdapter(adapter)

        // Load existing transaction data
        originalTransaction = intent.getStringExtra("transaction")
        originalTransaction?.let {
            val (title, amount, category, _, type) = it.split("|")
            etTitle.setText(title)
            etAmount.setText(amount)
            categoryAutoComplete.setText(category, false)
        }

        btnUpdate.setOnClickListener {
            updateTransaction()
        }
    }

    private fun updateTransaction() {
        val title = etTitle.text.toString()
        val amount = etAmount.text.toString().toFloatOrNull()
        val category = categoryAutoComplete.text.toString()
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        if (title.isNotEmpty() && amount != null && category.isNotEmpty()) {
            originalTransaction?.let { original ->
                val (_, _, _, _, type) = original.split("|")
                val newTransaction = "$title|$amount|$category|$date|$type"

                val transactions = sharedPreferences.getStringSet("transactions", mutableSetOf())!!.toMutableSet()
                transactions.remove(original)
                transactions.add(newTransaction)
                sharedPreferences.edit().putStringSet("transactions", transactions).apply()

                val currentBudget = sharedPreferences.getFloat("currentBudget", 0.0f)
                val originalAmount = original.split("|")[1].toFloat()
                val newBudget = if (type == "Income") {
                    currentBudget - originalAmount + amount
                } else {
                    currentBudget + originalAmount - amount
                }
                sharedPreferences.edit().putFloat("currentBudget", newBudget).apply()

                Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }
}