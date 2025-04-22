package com.example.moneymate

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class TransactionAdapter(
    private val context: Context,
    private val transactions: List<Transaction>,
    private val onDeleteClick: (Transaction) -> Unit,
    private val onUpdateClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCategory: ImageView = view.findViewById(R.id.imgCategory)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtType: TextView = view.findViewById(R.id.txtType)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
        val btnUpdate: Button = view.findViewById(R.id.btnUpdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        // Set the category image dynamically, with fallback
        val resourceName = "${transaction.category.lowercase(Locale.ROOT)}_${transaction.type.lowercase(Locale.ROOT)}"
        val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resourceId != 0) {
            holder.imgCategory.setImageResource(resourceId)
        } else {
            holder.imgCategory.setImageResource(R.drawable.other_income) // fallback image
        }

        holder.txtCategory.text = transaction.category
        holder.txtType.text = transaction.type
        holder.txtDate.text = transaction.date

        if (transaction.type.equals("Income", ignoreCase = true)) {
            holder.txtAmount.setTextColor(Color.parseColor("#2563EB")) // Blue
            holder.txtAmount.text = "+ Rs. ${transaction.amount}"
        } else {
            holder.txtAmount.setTextColor(Color.parseColor("#DC2626")) // Red
            holder.txtAmount.text = "- Rs. ${transaction.amount}"
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(transaction)
        }

        holder.btnUpdate.setOnClickListener {
            onUpdateClick(transaction)
        }
    }

    override fun getItemCount() = transactions.size
}
