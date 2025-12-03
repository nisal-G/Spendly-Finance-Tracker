package com.example.spendly

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendly.AddTransactionActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class NotificationsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnClearAll: MaterialButton
    private lateinit var emptyStateLayout: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView)
        btnClearAll = findViewById(R.id.btnClearAll)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)



        recyclerView.layoutManager = LinearLayoutManager(this)
        displayNotifications()

        btnClearAll.setOnClickListener {
            clearAllNotifications()
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_notifications

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
                R.id.nav_view_transactions -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    true
                }
                R.id.nav_notifications -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun displayNotifications() {
        val notifications = sharedPreferences.getStringSet("notifications", setOf())!!.toMutableList()

        if (notifications.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            btnClearAll.visibility = View.GONE
        } else {
            emptyStateLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            btnClearAll.visibility = View.VISIBLE

            recyclerView.adapter = NotificationAdapter(notifications) { notification ->
                removeNotification(notification)
            }
        }
    }

    private fun removeNotification(notification: String) {
        val notifications = sharedPreferences.getStringSet("notifications", mutableSetOf())!!.toMutableSet()
        notifications.remove(notification)
        sharedPreferences.edit().putStringSet("notifications", notifications).apply()
        displayNotifications()
    }

    private fun clearAllNotifications() {
        sharedPreferences.edit().remove("notifications").apply()
        displayNotifications()
    }
}