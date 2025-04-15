package com.example.moneymate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my-database"
        ).build()

        userDao = db.userDao()
        recyclerView = findViewById(R.id.recyclerView)
        adapter = UserAdapter(emptyList())
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            // Insert some users
            userDao.insertUser(User(name = "Alice", age = 25))
            userDao.insertUser(User(name = "Bob", age = 30))
            userDao.insertUser(User(name = "Osama", age = 28))

            // Get and display users
            val users = userDao.getAllUsers()
            adapter.setData(users)
        }
    }
}
