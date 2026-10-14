package com.zero.banking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zero.banking.databinding.ActivityAccountInfoBinding

class AccountInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountInfoBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadUserData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Mapping fields based on RegisterActivity.kt
                    // name, email, phone
                    val name = document.getString("name") ?: "-"
                    val email = document.getString("email") ?: "-"
                    val phone = document.getString("phone") ?: "-"

                    binding.tvInfoName.text = name
                    binding.tvInfoEmail.text = email
                    binding.tvInfoPhone.text = phone
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}