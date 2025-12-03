package com.example.spendly

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val currentDateTime = "2025-04-02 20:10:48"
    private var loginAttempts = 0
    private var lastLoginAttemptTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize views using Material Design components
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvSignup = findViewById<TextView>(R.id.tvSignup)

        // Check if user is already logged in
        if (checkExistingSession()) {
            navigateToMain()
            return
        }

        btnLogin.setOnClickListener {
            if (validateLoginAttempts()) {
                if (validateInputs(etUsername, etPassword)) {
                    handleLogin(
                        etUsername.text.toString().trim(),
                        etPassword.text.toString()
                    )
                }
            } else {
                showLoginTimeoutDialog()
            }
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // Add text change listeners for real-time validation
        etUsername.addTextChangedListener(createTextWatcher(etUsername, "username"))
        etPassword.addTextChangedListener(createTextWatcher(etPassword, "password"))
    }

    private fun validateInputs(
        etUsername: TextInputEditText,
        etPassword: TextInputEditText
    ): Boolean {
        var isValid = true

        // Username validation
        val username = etUsername.text.toString().trim()
        when {
            username.isEmpty() -> {
                etUsername.error = "Username is required"
                isValid = false
            }
            username.length < 3 -> {
                etUsername.error = "Invalid username format"
                isValid = false
            }
        }

        // Password validation
        val password = etPassword.text.toString()
        when {
            password.isEmpty() -> {
                etPassword.error = "Password is required"
                isValid = false
            }
            password.length < 8 -> {
                etPassword.error = "Invalid password format"
                isValid = false
            }
        }

        return isValid
    }

    private fun handleLogin(username: String, password: String) {
        val storedUsername = sharedPreferences.getString("username", "")
        val storedPassword = sharedPreferences.getString("password", "")

        if (username == storedUsername && password == storedPassword) {
            // Login successful
            loginAttempts = 0 // Reset login attempts
            createUserSession(username)
            navigateToMain()
        } else {
            // Login failed
            handleFailedLogin()
        }
    }

    private fun handleFailedLogin() {
        loginAttempts++
        lastLoginAttemptTime = System.currentTimeMillis()

        val remainingAttempts = 3 - loginAttempts

        if (remainingAttempts > 0) {
            showAlert(
                "Login Failed",
                "Invalid username or password. $remainingAttempts attempts remaining."
            )
        } else {
            showLoginTimeoutDialog()
        }
    }

    private fun validateLoginAttempts(): Boolean {
        if (loginAttempts >= 3) {
            val timeDiff = System.currentTimeMillis() - lastLoginAttemptTime
            if (timeDiff < 300000) { // 5 minutes timeout
                return false
            }
            // Reset attempts after timeout
            loginAttempts = 0
        }
        return true
    }

    private fun showLoginTimeoutDialog() {
        val timeRemaining = (300000 - (System.currentTimeMillis() - lastLoginAttemptTime)) / 1000
        AlertDialog.Builder(this)
            .setTitle("Login Temporarily Disabled")
            .setMessage("Too many failed attempts. Please try again after ${timeRemaining} seconds.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun createUserSession(username: String) {
        sharedPreferences.edit {
            putBoolean("isLoggedIn", true)
            putString("loggedInUsername", username)
            putString("lastLoginTime", currentDateTime)
            apply()
        }
    }

    private fun checkExistingSession(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun createTextWatcher(
        editText: TextInputEditText,
        field: String
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when (field) {
                    "username" -> {
                        if (s.toString().trim().isNotEmpty() && s.toString().trim().length < 3) {
                            editText.error = "Username must be at least 3 characters"
                        }
                    }
                    "password" -> {
                        if (s.toString().isNotEmpty() && s.toString().length < 8) {
                            editText.error = "Password must be at least 8 characters"
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        // Show exit confirmation
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}