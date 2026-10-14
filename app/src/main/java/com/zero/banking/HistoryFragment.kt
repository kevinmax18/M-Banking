package com.zero.banking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.zero.banking.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: TransactionAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private var allTransactions = mutableListOf<Transaction>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter(emptyList())
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        setupFilters()
        setupSwipeRefresh()
        loadTransactions()
    }

    private fun setupFilters() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener { _, checkedIds ->
            applyFilters(checkedIds.firstOrNull())
        }
    }

    private fun applyFilters(checkedId: Int?) {
        val filtered = when (checkedId) {
            R.id.chipIncome -> allTransactions.filter { it.type == "IN" }
            R.id.chipExpense -> allTransactions.filter { it.type == "OUT" }
            else -> allTransactions
        }
        adapter.updateData(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadTransactions()
        }
    }

    private fun loadTransactions() {
        val user = auth.currentUser ?: return
        
        binding.swipeRefresh.isRefreshing = true
        
        db.collection("transactions")
            .whereEqualTo("uid", user.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (isAdded) {
                    binding.swipeRefresh.isRefreshing = false
                    
                    if (e != null) {
                        Toast.makeText(requireContext(), "Gagal memuat riwayat: ${e.message}", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        allTransactions = snapshots.toObjects(Transaction::class.java).toMutableList()
                        applyFilters(binding.chipGroupFilters.checkedChipId)
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}