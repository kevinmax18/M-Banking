package com.zero.banking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup

class HistoryFragment : Fragment() {

    private lateinit var adapter: TransactionAdapter
    private val allTransactions = listOf(
        Transaction("Transfer ke Budi", "12 Okt 2023 - 09:15", "-Rp 500.000", false),
        Transaction("Gaji Bulanan", "10 Okt 2023 - 08:00", "+Rp 15.000.000", true),
        Transaction("Pembayaran Listrik", "08 Okt 2023 - 19:30", "-Rp 250.000", false),
        Transaction("Top Up e-Wallet", "05 Okt 2023 - 14:20", "-Rp 100.000", false),
        Transaction("Transfer Masuk", "01 Okt 2023 - 10:00", "+Rp 2.000.000", true)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvHistory = view.findViewById<RecyclerView>(R.id.rvHistory)
        adapter = TransactionAdapter(allTransactions)
        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        rvHistory.adapter = adapter

        view.findViewById<ChipGroup>(R.id.chipGroupFilters).setOnCheckedStateChangeListener { group, checkedIds ->
            val filtered = when (checkedIds.firstOrNull()) {
                R.id.chipIncome -> allTransactions.filter { it.isIncome }
                R.id.chipExpense -> allTransactions.filter { !it.isIncome }
                else -> allTransactions
            }
            adapter.updateData(filtered)
        }
    }
}