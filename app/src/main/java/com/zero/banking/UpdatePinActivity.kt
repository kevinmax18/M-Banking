package com.zero.banking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zero.banking.databinding.ActivityUpdatePinBinding

class UpdatePinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePinBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        binding.btnSavePin.setOnClickListener {
            handleUpdatePin()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun handleUpdatePin() {
        val currentPin = binding.etCurrentPin.text.toString().trim()
        val newPin = binding.etNewPin.text.toString().trim()
        val confirmPin = binding.etConfirmNewPin.text.toString().trim()

        if (currentPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
            Toast.makeText(this, R.string.error_pin_empty, Toast.LENGTH_SHORT).show()
            return
        }

        if (newPin.length != 6) {
            Toast.makeText(this, R.string.error_pin_length, Toast.LENGTH_SHORT).show()
            return
        }

        if (newPin != confirmPin) {
            Toast.makeText(this, R.string.error_pin_mismatch, Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Sesi berakhir", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnSavePin.isEnabled = false
        
        try {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val dbPin = snapshot.getString("pin_keamanan") ?: "123456"
                        if (currentPin == dbPin) {
                            updatePinInFirestore(uid, newPin)
                        } else {
                            binding.btnSavePin.isEnabled = true
                            Toast.makeText(this, R.string.error_current_pin_wrong, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.btnSavePin.isEnabled = true
                        Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    binding.btnSavePin.isEnabled = true
                    Toast.makeText(this, "Gagal verifikasi PIN: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            binding.btnSavePin.isEnabled = true
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePinInFirestore(uid: String, newPin: String) {
        try {
            db.collection("users").document(uid)
                .update("pin_keamanan", newPin)
                .addOnSuccessListener {
                    Toast.makeText(this, R.string.success_pin_updated, Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    binding.btnSavePin.isEnabled = true
                    Toast.makeText(this, "Gagal update PIN: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            binding.btnSavePin.isEnabled = true
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
