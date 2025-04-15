package com.example.moneymate


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val t = transactions[position]
        holder.txtDate.text = t.date
        holder.txtCategory.text = "${t.type} - ${t.category}"
        holder.txtAmount.text = "Rs. ${t.amount}"
    }

    override fun getItemCount() = transactions.size
}
