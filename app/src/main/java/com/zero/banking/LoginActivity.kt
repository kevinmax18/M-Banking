package com.zero.banking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(this)

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard()
        }

        val etEmail = findViewById<EditText>(R.id.etUsername) // Using etUsername ID for Email input
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegisterPrompt = findViewById<TextView>(R.id.tvRegisterPrompt)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Email dan Password tidak boleh kosong")
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        tvRegisterPrompt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                sessionManager.saveSession(email)
                showToast("Login Berhasil")
                navigateToDashboard()
            }
            .addOnFailureListener { e ->
                val message = when (e) {
                    is FirebaseAuthInvalidUserException -> "Email tidak terdaftar."
                    is FirebaseAuthInvalidCredentialsException -> "Password salah."
                    else -> e.message ?: "Login gagal"
                }
                showToast(message)
            }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
