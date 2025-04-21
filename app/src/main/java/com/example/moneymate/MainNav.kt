package com.example.moneymate

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainNav : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)

        // Load default fragment
        replaceFragment(HomeFragment())

        // Set up buttons
        findViewById<ImageButton>(R.id.nav_home).setOnClickListener {
            replaceFragment(HomeFragment())
        }
        findViewById<ImageButton>(R.id.nav_add).setOnClickListener {
            replaceFragment(AddFragment())
        }
        findViewById<ImageButton>(R.id.nav_summary).setOnClickListener {
            replaceFragment(SummaryFragment())
        }
//         findViewById<ImageButton>(R.id.nav_budget).setOnClickListener {
//           replaceFragment(BudgetFragment())
//       }
        findViewById<ImageButton>(R.id.nav_settings).setOnClickListener {
            replaceFragment(SettingsFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
