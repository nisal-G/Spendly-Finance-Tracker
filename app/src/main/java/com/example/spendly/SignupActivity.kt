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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SignupActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val currentDateTime = "2025-04-21 20:08:39"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize views using Material Design components
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnSignup = findViewById<MaterialButton>(R.id.btnSignup)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        btnSignup.setOnClickListener {
            if (validateInputs(etUsername, etEmail, etPassword, etConfirmPassword)) {
                handleSignup(
                    etUsername.text.toString(),
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )
            }
        }

        tvLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun validateInputs(
        etUsername: TextInputEditText,
        etEmail: TextInputEditText,
        etPassword: TextInputEditText,
        etConfirmPassword: TextInputEditText
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
                etUsername.error = "Username must be at least 3 characters"
                isValid = false
            }
            username.length > 20 -> {
                etUsername.error = "Username must be less than 20 characters"
                isValid = false
            }
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> {
                etUsername.error = "Username can only contain letters, numbers, and underscores"
                isValid = false
            }
            // Check if username already exists
            sharedPreferences.getString("username", "") == username -> {
                etUsername.error = "Username already exists"
                isValid = false
            }
        }

        // Email validation
        val email = etEmail.text.toString().trim()
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        when {
            email.isEmpty() -> {
                etEmail.error = "Email is required"
                isValid = false
            }
            !email.matches(emailPattern) -> {
                etEmail.error = "Invalid email address"
                isValid = false
            }
            // Check if email already exists
            sharedPreferences.getString("email", "") == email -> {
                etEmail.error = "Email already registered"
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
                etPassword.error = "Password must be at least 8 characters"
                isValid = false
            }
            !password.matches(Regex(".*[A-Z].*")) -> {
                etPassword.error = "Password must contain at least one uppercase letter"
                isValid = false
            }
            !password.matches(Regex(".*[a-z].*")) -> {
                etPassword.error = "Password must contain at least one lowercase letter"
                isValid = false
            }
            !password.matches(Regex(".*[0-9].*")) -> {
                etPassword.error = "Password must contain at least one number"
                isValid = false
            }
            !password.matches(Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) -> {
                etPassword.error = "Password must contain at least one special character"
                isValid = false
            }
        }

        // Confirm password validation
        val confirmPassword = etConfirmPassword.text.toString()
        when {
            confirmPassword.isEmpty() -> {
                etConfirmPassword.error = "Please confirm your password"
                isValid = false
            }
            confirmPassword != password -> {
                etConfirmPassword.error = "Passwords do not match"
                isValid = false
            }
        }

        return isValid
    }

    private fun handleSignup(username: String, email: String, password: String) {
        try {
            // Store user credentials
            sharedPreferences.edit {
                putString("username", username)
                putString("email", email)
                putString("password", password)
                putString("registrationDate", currentDateTime)
                apply()
            }

            // Show success message
            Toast.makeText(
                this,
                "Account created successfully! Please login.",
                Toast.LENGTH_LONG
            ).show()

            // Navigate to login page
            navigateToLogin()
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Error creating account: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // Add confirmation dialog when user tries to leave the signup process
    override fun onBackPressed() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Cancel Registration")
            .setMessage("Are you sure you want to cancel the registration process?")
            .setPositiveButton("Yes") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("No", null)
            .show()
    }
}