package com.zero.banking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.zero.banking.databinding.FragmentHomeBinding
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isBalanceVisible = true
    private var currentBalance: Long = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var balanceListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set status switch berdasarkan mode saat ini
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        binding.themeToggle.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_YES

        // Tangkap data username dari Activity atau Arguments
        val username = arguments?.getString("ARG_USERNAME") ?: activity?.intent?.getStringExtra("EXTRA_USERNAME")
        val displayName = if (username.isNullOrBlank()) getString(R.string.default_username) else username
        binding.tvUserName.text = getString(R.string.dashboard_hello, displayName.uppercase())

        // Firebase Initialization
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Setup Swipe Refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        // Real-time Balance Listener
        val uid = auth.currentUser?.uid
        if (uid != null) {
            setupBalanceListener(uid)
        }

        binding.themeToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.btnToggleBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            updateBalanceDisplay()
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

        binding.btnTopUp.setOnClickListener {
            startActivity(Intent(requireContext(), TopUpActivity::class.java))
        }

        binding.btnTransfer.setOnClickListener(dummyClick)
        binding.btnBill.setOnClickListener(dummyClick)
        binding.btnHistory.setOnClickListener(dummyClick)

        binding.tvSeeAllTransactions.setOnClickListener {
            Toast.makeText(context, R.string.toast_see_all_transactions, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBalanceListener(uid: String) {
        balanceListener?.remove()
        balanceListener = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    currentBalance = (snapshot.get("saldo_awal") as? Number)?.toLong() ?: 0L
                    updateBalanceDisplay()
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }
    }

    private fun refreshData() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            // Force re-fetch by re-attaching listener or just letting the listener do its work
            // For a "manual sync" feel, we can just re-trigger the fetch
            db.collection("users").document(uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        currentBalance = (snapshot.get("saldo_awal") as? Number)?.toLong() ?: 0L
                        updateBalanceDisplay()
                    }
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(context, "Data updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(context, "Failed to sync", Toast.LENGTH_SHORT).show()
                }
        } else {
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun updateBalanceDisplay() {
        if (_binding == null) return
        if (isBalanceVisible) {
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            binding.tvBalanceValue.text = formatRupiah.format(currentBalance).replace(",00", "").replace("Rp", "Rp ")
        } else {
            binding.tvBalanceValue.text = "********"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        balanceListener?.remove()
        _binding = null
    }
}