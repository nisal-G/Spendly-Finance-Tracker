package com.example.spendly

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide the status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        // Initialize views
        val ivLogo: ImageView = findViewById(R.id.ivLogo)
        val tvAppName: TextView = findViewById(R.id.tvAppName)
        val tvTagline: TextView = findViewById(R.id.tvTagline)
        val btnGetStarted: Button = findViewById(R.id.btnGetStarted)

        // Create animations
        val fadeIn = AnimatorInflater.loadAnimator(this, R.animator.fade_in) as ObjectAnimator
        val scaleUp = AnimatorSet().apply {
            playTogether(
                AnimatorInflater.loadAnimator(this@SplashActivity, R.animator.scale_x) as ObjectAnimator,
                AnimatorInflater.loadAnimator(this@SplashActivity, R.animator.scale_y) as ObjectAnimator
            )
        }

        // Apply animations sequentially
        fadeIn.apply {
            target = ivLogo
            start()
        }

        scaleUp.apply {
            setTarget(ivLogo)
            startDelay = 500
            start()
        }

        ObjectAnimator.ofFloat(tvAppName, "translationY", 100f, 0f).apply {
            duration = 1000
            startDelay = 1000
            start()
        }

        ObjectAnimator.ofFloat(tvAppName, "alpha", 0f, 1f).apply {
            duration = 1000
            startDelay = 1000
            start()
        }

        ObjectAnimator.ofFloat(tvTagline, "translationY", 100f, 0f).apply {
            duration = 1000
            startDelay = 1500
            start()
        }

        ObjectAnimator.ofFloat(tvTagline, "alpha", 0f, 1f).apply {
            duration = 1000
            startDelay = 1500
            start()
        }

        ObjectAnimator.ofFloat(btnGetStarted, "alpha", 0f, 1f).apply {
            duration = 1000
            startDelay = 2000
            start()
        }

        // Set click listener for Get Started button
        btnGetStarted.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            // Add fade transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}