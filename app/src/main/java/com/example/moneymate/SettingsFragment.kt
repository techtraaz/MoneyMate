package com.example.moneymate

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class SettingsFragment : Fragment() {

    private lateinit var btnDownloadJson: Button
    private val storage: TransactionStorage by lazy {
        TransactionStorage(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize button
        btnDownloadJson = view.findViewById(R.id.btnDownloadJson)

        // Set click listener for the button
        btnDownloadJson.setOnClickListener {
            downloadTransactionsAsJson()
        }

        return view
    }

    private fun downloadTransactionsAsJson() {
        val transactions = storage.getTransactions()

        if (transactions.isNotEmpty()) {
            // Convert transactions to JSON
            val gson = Gson()
            val json = gson.toJson(transactions)

            // Save the file in internal storage
            val file = File(requireContext().filesDir, "transactions.json")

            try {
                // Create file and write the JSON data to internal storage
                val fileOutputStream = FileOutputStream(file)
                val outputStreamWriter = OutputStreamWriter(fileOutputStream)
                outputStreamWriter.write(json)
                outputStreamWriter.flush()
                outputStreamWriter.close()

                // Show success message
                Toast.makeText(requireContext(), "Transactions saved to internal storage.", Toast.LENGTH_SHORT).show()

                // Allow the user to download the file (using intent)
                downloadFile(file)
            } catch (e: Exception) {
                // Show error message
                Toast.makeText(requireContext(), "Error saving transactions: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No transactions to download.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadFile(file: File) {
        try {
            // Open the file and create an Intent to let the user download it
            val fileUri = android.net.Uri.fromFile(file)
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, "application/json")
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: Exception) {
            // Handle the case where the file download doesn't work
            Toast.makeText(requireContext(), "Error opening the file for download.", Toast.LENGTH_SHORT).show()
        }
    }
}
