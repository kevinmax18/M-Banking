package com.zero.banking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zero.banking.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.itemView.context
        
        holder.binding.tvTransactionTitle.text = transaction.title
        
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        holder.binding.tvTransactionDate.text = transaction.timestamp?.toDate()?.let { sdf.format(it) } ?: "-"

        val localeID = Locale("id", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        val amountFormatted = formatRupiah.format(transaction.amount).replace("Rp", "Rp ")
        
        if (transaction.type == "IN") {
            holder.binding.tvTransactionAmount.text = "+$amountFormatted"
            holder.binding.tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.income))
        } else {
            holder.binding.tvTransactionAmount.text = "-$amountFormatted"
            holder.binding.tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.expense))
        }
    }

    override fun getItemCount() = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}