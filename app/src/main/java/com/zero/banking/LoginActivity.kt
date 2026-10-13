package com.zero.banking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername) // Pastikan ID sesuaian dengan XML Anda
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val usernameInput = etUsername.text.toString().trim()
            
            // Simpan sesi login
            val sessionManager = SessionManager(this)
            sessionManager.saveSession(usernameInput)

            val intent = Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("EXTRA_USERNAME", usernameInput)
            }
            startActivity(intent)
        }

        findViewById<TextView>(R.id.tvRegisterPrompt).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
