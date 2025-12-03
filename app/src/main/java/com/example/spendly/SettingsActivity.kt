package com.example.spendly

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.spendly.AddTransactionActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val tvUsername: TextView = findViewById(R.id.tvUsername)
        val tvEmail: TextView = findViewById(R.id.tvEmail)
        val etCurrentPassword: EditText = findViewById(R.id.etCurrentPassword)
        val etNewPassword: EditText = findViewById(R.id.etNewPassword)
        val etConfirmNewPassword: EditText = findViewById(R.id.etConfirmNewPassword)
        val btnResetPassword: Button = findViewById(R.id.btnResetPassword)
        val btnResetApp: Button = findViewById(R.id.btnResetApp)
        val btnLogout: Button = findViewById(R.id.btnLogout)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Display current user's username and email
        val username = sharedPreferences.getString("username", "N/A")
        val email = sharedPreferences.getString("email", "N/A")
        tvUsername.text = "Username: $username"
        tvEmail.text = "Email: $email"

        btnResetPassword.setOnClickListener {
            val currentPassword = etCurrentPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmNewPassword = etConfirmNewPassword.text.toString()

            val storedPassword = sharedPreferences.getString("password", "")

            if (currentPassword == storedPassword) {
                if (newPassword == confirmNewPassword) {
                    sharedPreferences.edit {
                        putString("password", newPassword)
                        apply()
                    }
                    Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
            }
        }

        btnResetApp.setOnClickListener {
            resetAppData()
        }

        btnLogout.setOnClickListener {
            sharedPreferences.edit {
                remove("isLoggedIn")
                remove("loggedInUsername")
                apply()
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set the selected item to highlight the settings icon
        bottomNavigationView.selectedItemId = R.id.nav_settings

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
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    // Already on the settings page
                    true
                }
                else -> false
            }
        }
    }

    private fun resetAppData() {
        // Get the stored user details
        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")
        val password = sharedPreferences.getString("password", "")

        // Clear all data
        sharedPreferences.edit().clear().apply()

        // Restore user details
        sharedPreferences.edit {
            putString("username", username)
            putString("email", email)
            putString("password", password)
            apply()
        }

        Toast.makeText(this, "App data reset successfully", Toast.LENGTH_SHORT).show()
    }
}