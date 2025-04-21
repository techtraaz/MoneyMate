package com.example.moneymate


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private val transactions: List<Transaction> ,
                         private val onDeleteClick: (Transaction) -> Unit,
                         private val onUpdateClick: (Transaction) -> Unit) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
        val btnUpdate: Button = itemView.findViewById(R.id.btnUpdate)

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
        holder.btnDelete.setOnClickListener {
            onDeleteClick(t)
        }
        holder.btnUpdate.setOnClickListener {
            onUpdateClick(t)
        }
    }

    override fun getItemCount() = transactions.size
}
