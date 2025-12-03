package com.example.spendly

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendly.AddTransactionActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*


class TransactionsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var spinnerFilter: Spinner // Keep as Spinner
    private lateinit var spinnerSort: Spinner // Keep as Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvDateTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize views
        spinnerFilter = findViewById(R.id.spinnerFilter)
        spinnerSort = findViewById(R.id.spinnerSort)
        recyclerView = findViewById(R.id.recyclerView)
        tvDateTime = findViewById(R.id.tvDateTime)

        // Set current date time
        tvDateTime.text = "2025-04-21 18:41:00"

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup filter spinner
        val filterOptions = arrayOf("All", "Income", "Expenses")
        val filterAdapter = ArrayAdapter(this, R.layout.dropdown_item, filterOptions)
        filterAdapter.setDropDownViewResource(R.layout.dropdown_item)
        spinnerFilter.adapter = filterAdapter

        // Setup sort spinner
        val sortOptions = arrayOf("Latest", "Oldest")
        val sortAdapter = ArrayAdapter(this, R.layout.dropdown_item, sortOptions)
        sortAdapter.setDropDownViewResource(R.layout.dropdown_item)
        spinnerSort.adapter = sortAdapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                displayTransactions()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                displayTransactions()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        displayTransactions()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_view_transactions

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_add_transaction -> {
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                R.id.nav_view_transactions -> true
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

    private fun displayTransactions() {
        val filter = spinnerFilter.selectedItem.toString()
        val sort = spinnerSort.selectedItem.toString()
        val transactions = sharedPreferences.getStringSet("transactions", setOf())!!.toMutableList()

        when (sort) {
            "Latest" -> transactions.sortByDescending { transaction ->
                val (_, _, _, date, _) = transaction.split("|")
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
            }
            "Oldest" -> transactions.sortBy { transaction ->
                val (_, _, _, date, _) = transaction.split("|")
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
            }
        }

        val filteredTransactions = when (filter) {
            "Income" -> transactions.filter { it.contains("Income") }
            "Expenses" -> transactions.filter { it.contains("Expense") }
            else -> transactions
        }

        val transactionAdapter = TransactionAdapter(filteredTransactions, ::onEditTransaction, ::onDeleteTransaction)
        recyclerView.adapter = transactionAdapter
    }

    private fun onEditTransaction(transaction: String) {
        val intent = Intent(this, EditTransactionActivity::class.java)
        intent.putExtra("transaction", transaction)
        startActivity(intent)
    }

    private fun onDeleteTransaction(transaction: String) {
        val transactions = sharedPreferences.getStringSet("transactions", mutableSetOf())!!.toMutableSet()
        transactions.remove(transaction)
        sharedPreferences.edit().putStringSet("transactions", transactions).apply()

        val (_, amount, _, _, type) = transaction.split("|")
        val currentBudget = sharedPreferences.getFloat("currentBudget", 0.0f)
        val newBudget = if (type == "Income") currentBudget - amount.toFloat() else currentBudget + amount.toFloat()
        sharedPreferences.edit().putFloat("currentBudget", newBudget).apply()

        displayTransactions()
    }
}