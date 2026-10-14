package com.zero.banking

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPinKeamanan = findViewById<EditText>(R.id.etPinKeamanan)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val pin = etPinKeamanan.text.toString().trim()

            if (validateInput(name, email, phone, password, pin)) {
                registerUser(name, email, phone, password, pin)
            }
        }

        findViewById<TextView>(R.id.tvLoginPrompt).setOnClickListener {
            finish()
        }
    }

    private fun validateInput(name: String, email: String, phone: String, pass: String, pin: String): Boolean {
        if (name.isEmpty()) {
            showToast("Nama lengkap tidak boleh kosong")
            return false
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email tidak valid")
            return false
        }
        if (phone.isEmpty()) {
            showToast("Nomor telepon tidak boleh kosong")
            return false
        }
        if (pass.length < 6) {
            showToast("Password minimal 6 karakter")
            return false
        }
        if (pin.length != 6) {
            showToast("PIN harus 6 digit")
            return false
        }
        return true
    }

    private fun registerUser(name: String, email: String, phone: String, pass: String, pin: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    saveUserToFirestore(uid, name, email, phone, pin)
                }
            }
            .addOnFailureListener { e ->
                val errorMessage = when (e) {
                    is FirebaseAuthUserCollisionException -> "Email sudah terdaftar"
                    else -> "Registrasi gagal: ${e.localizedMessage}"
                }
                showToast(errorMessage)
            }
    }

    private fun saveUserToFirestore(uid: String, name: String, email: String, phone: String, pin: String) {
        val userMap = hashMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "pin_keamanan" to pin,
            "saldo_awal" to 0,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("users").document(uid)
            .set(userMap)
            .addOnSuccessListener {
                showToast("Akun berhasil dibuat")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                showToast("Gagal menyimpan data: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
