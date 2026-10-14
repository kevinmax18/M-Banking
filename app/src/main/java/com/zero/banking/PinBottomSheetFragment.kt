package com.zero.banking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zero.banking.databinding.FragmentPinBottomSheetBinding

class PinBottomSheetFragment(private val onPinConfirmed: () -> Unit) : BottomSheetDialogFragment() {

    private var _binding: FragmentPinBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnConfirmPin.setOnClickListener {
            val enteredPin = binding.etPin.text.toString()
            if (enteredPin.length == 6) {
                validatePinFromFirestore(enteredPin)
            } else {
                Toast.makeText(requireContext(), "Masukkan 6 digit PIN", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validatePinFromFirestore(enteredPin: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        binding.btnConfirmPin.isEnabled = false
        db.collection("users").document(uid).get()
            .addOnSuccessListener { snapshot ->
                binding.btnConfirmPin.isEnabled = true
                if (snapshot.exists()) {
                    val correctPin = snapshot.getString("pin_keamanan") ?: "123456" // Fallback for legacy
                    if (enteredPin == correctPin) {
                        onPinConfirmed()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "PIN Salah!", Toast.LENGTH_SHORT).show()
                        binding.etPin.text?.clear()
                    }
                } else {
                    Toast.makeText(requireContext(), "Data user tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                binding.btnConfirmPin.isEnabled = true
                Toast.makeText(requireContext(), "Gagal memverifikasi PIN: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PinBottomSheetFragment"
    }
}
