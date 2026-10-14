package com.zero.banking

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.zero.banking.databinding.ActivityTopUpBinding
import java.text.NumberFormat
import java.util.Locale

class TopUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopUpBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupPaymentMethod()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupPaymentMethod() {
        val methods = arrayOf("Virtual Account BCA", "Virtual Account Mandiri", "Virtual Account BNI", "Indomaret/Alfamart")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, methods)
        binding.actvPaymentMethod.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.etNominal.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    binding.etNominal.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[^\\d]".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val localeID = Locale("id", "ID")
                        val formatted = NumberFormat.getNumberInstance(localeID).format(parsed)

                        current = formatted
                        binding.etNominal.setText(formatted)
                        binding.etNominal.setSelection(formatted.length)
                    } else {
                        current = ""
                        binding.etNominal.setText("")
                    }

                    binding.etNominal.addTextChangedListener(this)
                }
            }
        })

        binding.chipGroupNominal.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChip = when (checkedIds.first()) {
                    R.id.chip50 -> "50.000"
                    R.id.chip100 -> "100.000"
                    R.id.chip200 -> "200.000"
                    R.id.chip500 -> "500.000"
                    else -> ""
                }
                binding.etNominal.setText(selectedChip)
            }
        }

        binding.btnContinue.setOnClickListener {
            val rawNominal = binding.etNominal.text.toString().replace("[^\\d]".toRegex(), "")
            
            if (rawNominal.isEmpty()) {
                Toast.makeText(this, "Masukkan nominal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nominal = rawNominal.toLong()
            if (nominal < 10000) {
                Toast.makeText(this, "Minimal Top Up Rp 10.000", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showPinDialog(nominal)
        }
    }

    private fun showPinDialog(nominal: Long) {
        val pinSheet = PinBottomSheetFragment {
            executeTopUp(nominal)
        }
        pinSheet.show(supportFragmentManager, PinBottomSheetFragment.TAG)
    }

    private fun executeTopUp(nominal: Long) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Sesi berakhir, silakan login ulang", Toast.LENGTH_SHORT).show()
            return
        }

        val batch = db.batch()
        val userRef = db.collection("users").document(user.uid)
        val txRef = db.collection("transactions").document()

        batch.update(userRef, "saldo_awal", FieldValue.increment(nominal))
        
        val transactionData = hashMapOf(
            "uid" to user.uid,
            "title" to "Top Up Saldo",
            "amount" to nominal,
            "type" to "IN",
            "timestamp" to FieldValue.serverTimestamp()
        )
        batch.set(txRef, transactionData)

        batch.commit()
            .addOnSuccessListener {
                if (!isFinishing) {
                    showSuccessDialog(nominal)
                }
            }
            .addOnFailureListener { e ->
                if (!isFinishing) {
                    Toast.makeText(this, "Gagal Top Up: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showSuccessDialog(nominal: Long) {
        AlertDialog.Builder(this)
            .setTitle("Top Up Berhasil")
            .setMessage("Selamat! Saldo sebesar Rp $nominal berhasil ditambahkan ke akun Anda.")
            .setCancelable(false)
            .setPositiveButton("Ke Dashboard") { _, _ ->
                val intent = Intent(this, DashboardActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            .show()
    }
}
