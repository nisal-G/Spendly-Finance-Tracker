package com.example.spendly

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class BudgetUpdateActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etBudget: TextInputEditText
    private lateinit var btnSaveBudget: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_update)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize views
        etBudget = findViewById(R.id.etBudget)
        btnSaveBudget = findViewById(R.id.btnSaveBudget)



        // Setup back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }

        // Set current budget as hint
        val currentBudget = sharedPreferences.getFloat("currentBudget", 0.0f)
        etBudget.hint = "Current: Rs.${String.format("%.2f", currentBudget)}"

        btnSaveBudget.setOnClickListener {
            saveBudget()
        }
    }

    private fun saveBudget() {
        val budgetText = etBudget.text.toString()
        val budget = budgetText.toFloatOrNull()

        if (budgetText.isEmpty()) {
            etBudget.error = "Please enter a budget amount"
            return
        }

        if (budget == null || budget <= 0) {
            etBudget.error = "Please enter a valid budget amount"
            return
        }

        // Store the budget
        sharedPreferences.edit {
            putFloat("currentBudget", budget)
            apply()
        }

        // Show success message
        Toast.makeText(this, "Budget updated successfully", Toast.LENGTH_SHORT).show()

        // Go back to main screen
        finish()
    }
}