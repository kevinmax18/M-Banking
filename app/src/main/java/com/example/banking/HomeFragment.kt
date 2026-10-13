package com.example.banking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private var isBalanceVisible = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themeToggle = view.findViewById<SwitchCompat>(R.id.themeToggle)
        val btnTransfer = view.findViewById<ImageButton>(R.id.btnTransfer)
        val btnBill = view.findViewById<ImageButton>(R.id.btnBill)
        val btnTopUp = view.findViewById<ImageButton>(R.id.btnTopUp)
        val btnHistory = view.findViewById<ImageButton>(R.id.btnHistory)
        val btnToggleBalance = view.findViewById<ImageButton>(R.id.btnToggleBalance)
        val tvBalanceValue = view.findViewById<TextView>(R.id.tvBalanceValue)
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvSeeAll = view.findViewById<TextView>(R.id.tvSeeAllTransactions)

        // Set status switch berdasarkan mode saat ini
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        themeToggle.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_YES

        // Tangkap data username dari Activity atau Arguments
        val username = arguments?.getString("ARG_USERNAME") ?: activity?.intent?.getStringExtra("EXTRA_USERNAME")
        val displayName = if (username.isNullOrBlank()) getString(R.string.default_username) else username
        tvUserName.text = getString(R.string.dashboard_hello, displayName.uppercase())

        themeToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        btnToggleBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            if (isBalanceVisible) {
                tvBalanceValue.text = "Rp 15.750.000"
            } else {
                tvBalanceValue.text = "********"
            }
        }

        val dummyClick = View.OnClickListener { v ->
            val nameRes = when (v.id) {
                R.id.btnTransfer -> R.string.menu_transfer
                R.id.btnBill -> R.string.menu_bill
                R.id.btnTopUp -> R.string.menu_topup
                R.id.btnHistory -> R.string.menu_history
                else -> R.string.home_main_menu
            }
            Toast.makeText(context, nameRes, Toast.LENGTH_SHORT).show()
        }

        btnTransfer.setOnClickListener(dummyClick)
        btnBill.setOnClickListener(dummyClick)
        btnTopUp.setOnClickListener(dummyClick)
        btnHistory.setOnClickListener(dummyClick)

        tvSeeAll.setOnClickListener {
            Toast.makeText(context, R.string.toast_see_all_transactions, Toast.LENGTH_SHORT).show()
        }
    }
}