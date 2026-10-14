package com.zero.banking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zero.banking.databinding.FragmentPinBottomSheetBinding

class PinBottomSheetFragment(private val onPinConfirmed: () -> Unit) : BottomSheetDialogFragment() {

    private var _binding: FragmentPinBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirmPin.setOnClickListener {
            val pin = binding.etPin.text.toString()
            if (pin == "123456") {
                onPinConfirmed()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "PIN Salah! Gunakan 123456 untuk tes.", Toast.LENGTH_SHORT).show()
            }
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
