package com.example.spendly

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendly.AddTransactionActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var pieChartExpenses: PieChart
    private lateinit var pieChartIncomes: PieChart
    private lateinit var rvRecentTransactions: RecyclerView
    private val currentDateTime = "2025-04-02 19:22:47"
    private val currentUser = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize views
        initializeViews()

        // Check if user is logged in
        if (!checkLoginStatus()) {
            navigateToLogin()
            return
        }

        // Setup UI components
        setupHeader()
        setupBudgetCard()
        setupRecentTransactions()
        setupCharts()
        setupNavigation()
        setupBackupRestore()
        setupUpdateBudgetCard()
    }

    private fun initializeViews() {
        pieChartExpenses = findViewById(R.id.pieChartExpenses)
        pieChartIncomes = findViewById(R.id.pieChartIncomes)
        rvRecentTransactions = findViewById(R.id.rvRecentTransactions)
    }

    private fun checkLoginStatus(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun setupHeader() {
        val tvUserDetails: TextView = findViewById(R.id.tvUserDetails)
        val tvCurrentDate: TextView = findViewById(R.id.tvCurrentDate)

        val loggedInUsername = sharedPreferences.getString("loggedInUsername", "Guest")
        tvUserDetails.text = "Welcome, $loggedInUsername"

        // Format current date
        val dateParts = currentDateTime.split(" ")[0].split("-")
        val monthYear = when(dateParts[1]) {
            "01" -> "January ${dateParts[0]}"
            "02" -> "February ${dateParts[0]}"
            "03" -> "March ${dateParts[0]}"
            "04" -> "April ${dateParts[0]}"
            "05" -> "May ${dateParts[0]}"
            "06" -> "June ${dateParts[0]}"
            "07" -> "July ${dateParts[0]}"
            "08" -> "August ${dateParts[0]}"
            "09" -> "September ${dateParts[0]}"
            "10" -> "October ${dateParts[0]}"
            "11" -> "November ${dateParts[0]}"
            "12" -> "December ${dateParts[0]}"
            else -> "Unknown"
        }
        tvCurrentDate.text = monthYear
    }

    private fun setupBudgetCard() {
        val tvCurrentBudget: TextView = findViewById(R.id.tvCurrentBudget)
        val tvRemainingDays: TextView = findViewById(R.id.tvRemainingDays)

        val currentBudget = sharedPreferences.getFloat("currentBudget", 0.0f)
        tvCurrentBudget.text = "Rs.${String.format("%.2f", currentBudget)}"

        val remainingDays = calculateRemainingDays()
        tvRemainingDays.text = "$remainingDays days remaining"
    }

    private fun setupRecentTransactions() {
        rvRecentTransactions.layoutManager = LinearLayoutManager(this)
        val transactions = sharedPreferences.getStringSet("transactions", setOf())!!
            .sortedByDescending { it.split("|")[3] }
            .take(5)

        rvRecentTransactions.adapter = RecentTransactionsAdapter(transactions)
    }

    private fun setupCharts() {
        pieChartExpenses.apply {
            description.isEnabled = false
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setEntryLabelColor(Color.BLACK)
            legend.textColor = resources.getColor(R.color.medium_purple, null)
            legend.textSize = 12f
            setUsePercentValues(true)
            holeRadius = 70f
            transparentCircleRadius = 75f
            centerText = "Expenses"
            setCenterTextColor(resources.getColor(R.color.vibrant_purple, null))
            setCenterTextSize(16f)
        }

        pieChartIncomes.apply {
            description.isEnabled = false
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setEntryLabelColor(Color.BLACK)
            legend.textColor = resources.getColor(R.color.medium_purple, null)
            legend.textSize = 12f
            setUsePercentValues(true)
            holeRadius = 70f
            transparentCircleRadius = 75f
            centerText = "Income"
            setCenterTextColor(resources.getColor(R.color.vibrant_purple, null))
            setCenterTextSize(16f)
        }

        val transactions = sharedPreferences.getStringSet("transactions", setOf())!!
        displayPieCharts(transactions)
    }

    private fun setupNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_add_transaction -> {
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
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

    private fun setupUpdateBudgetCard() {
        findViewById<CardView>(R.id.cardUpdateBudget).setOnClickListener {
            startActivity(Intent(this, BudgetUpdateActivity::class.java))
        }
    }

    private fun setupBackupRestore() {
        findViewById<MaterialButton>(R.id.btnBackupData).setOnClickListener {
            backupData()
        }

        findViewById<MaterialButton>(R.id.btnRestoreData).setOnClickListener {
            restoreData()
        }
    }

    private fun calculateRemainingDays(): Int {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        return lastDay - today
    }

    private fun displayPieCharts(transactions: Set<String>) {
        val expenseEntries = mutableListOf<PieEntry>()
        val incomeEntries = mutableListOf<PieEntry>()

        // Group transactions by category and calculate totals
        val expensesByCategory = mutableMapOf<String, Float>()
        val incomesByCategory = mutableMapOf<String, Float>()

        transactions.forEach { transaction ->
            val (_, amount, category, _, type) = transaction.split("|")
            val value = amount.toFloat()
            if (type == "Expense") {
                expensesByCategory[category] = (expensesByCategory[category] ?: 0f) + value
            } else if (type == "Income") {
                incomesByCategory[category] = (incomesByCategory[category] ?: 0f) + value
            }
        }

        // Create pie entries
        expensesByCategory.forEach { (category, amount) ->
            expenseEntries.add(PieEntry(amount, category))
        }

        incomesByCategory.forEach { (category, amount) ->
            incomeEntries.add(PieEntry(amount, category))
        }

        // Setup expense chart
        val expenseDataSet = PieDataSet(expenseEntries, "")
        expenseDataSet.apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 14f
            valueTextColor = Color.BLACK
            valueFormatter = PercentFormatter(pieChartExpenses)
        }

        val expenseData = PieData(expenseDataSet)
        pieChartExpenses.data = expenseData
        pieChartExpenses.invalidate()

        // Setup income chart
        val incomeDataSet = PieDataSet(incomeEntries, "")
        incomeDataSet.apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 14f
            valueTextColor = Color.BLACK
            valueFormatter = PercentFormatter(pieChartIncomes)
        }

        val incomeData = PieData(incomeDataSet)
        pieChartIncomes.data = incomeData
        pieChartIncomes.invalidate()
    }

    private fun backupData() {
        try {
            val transactions = sharedPreferences.getStringSet("transactions", setOf())!!
            val gson = Gson()
            val json = gson.toJson(transactions)

            val file = File(filesDir, "backup.json")
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos).use { osw ->
                    osw.write(json)
                }
            }

            Toast.makeText(this, "Data backed up successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreData() {
        try {
            val file = File(filesDir, "backup.json")
            if (file.exists()) {
                val json = FileInputStream(file).bufferedReader().use { it.readText() }
                val gson = Gson()
                val transactions: Set<String> = gson.fromJson(json, Set::class.java) as Set<String>

                sharedPreferences.edit().putStringSet("transactions", transactions).apply()
                Toast.makeText(this, "Data restored successfully", Toast.LENGTH_SHORT).show()

                // Refresh UI
                setupRecentTransactions()
                displayPieCharts(transactions)
                setupBudgetCard()
            } else {
                Toast.makeText(this, "No backup file found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        setupBudgetCard()
        setupRecentTransactions()
        setupCharts()
    }
}