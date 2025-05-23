package com.example.moneymate

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var txtMonthYear: TextView
    private lateinit var btnPrev: ImageView
    private lateinit var btnNext: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var storage: TransactionStorage
    private lateinit var addbtn: Button

    private lateinit var txtIncome: TextView
    private lateinit var txtExpense: TextView

    private var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtMonthYear = view.findViewById(R.id.txtMonthYear)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        recyclerView = view.findViewById(R.id.recyclerView)
        addbtn = view.findViewById(R.id.btnAddTransaction)
        txtIncome = view.findViewById(R.id.txtIncome)
        txtExpense = view.findViewById(R.id.txtExpense)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        storage = TransactionStorage(requireContext())

        btnPrev.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthYear()
        }

        btnNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthYear()
        }

        addbtn.setOnClickListener {
            val fragment = AddFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        updateMonthYear()
    }

    override fun onResume() {
        super.onResume()
        updateMonthYear()
    }

    private fun updateMonthYear() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        txtMonthYear.text = sdf.format(calendar.time)
        updateTransactionList()
    }

    private fun updateTransactionList() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val allTransactions = storage.getTransactions()

        val filtered = allTransactions.mapNotNull {
            try {
                val date = sdf.parse(it.date)
                val cal = Calendar.getInstance().apply { time = date!! }
                if (cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                ) {
                    it
                } else null
            } catch (e: Exception) {
                null
            }
        }.sortedByDescending {
            sdf.parse(it.date)
        }

        // Calculate totals
        val income = filtered.filter { it.type.equals("income", true) }.sumOf { it.amount }
        val expense = filtered.filter { it.type.equals("expense", true) }.sumOf { it.amount }

        txtIncome.text = "Rs.%.0f".format(income)
        txtExpense.text = "Rs.%.0f".format(expense)

        // Set adapter
        adapter = TransactionAdapter(
            requireContext(),
            filtered,
            onDeleteClick = { transactionToDelete ->
                storage.deleteTransactionById(transactionToDelete.id)
                updateTransactionList()
            },
            onUpdateClick = { transactionToEdit ->
                val fragment = AddFragment().apply {
                    arguments = Bundle().apply {
                        putInt("id", transactionToEdit.id)
                        putDouble("amount", transactionToEdit.amount)
                        putString("date", transactionToEdit.date)
                        putString("category", transactionToEdit.category)
                        putString("type", transactionToEdit.type)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        )

        recyclerView.adapter = adapter
    }
}
