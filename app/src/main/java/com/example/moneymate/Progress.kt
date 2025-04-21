package com.example.moneymate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


import android.widget.*
import android.view.View

class Progress : AppCompatActivity() {

    private var currentProgress = 0
    private val maxProgress = 100
    private lateinit var progressView: View
    private lateinit var btnIncrease: Button
    private lateinit var progressContainer: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_progress)


        progressView = findViewById(R.id.customProgress)
        btnIncrease = findViewById(R.id.btnIncrease)
        progressContainer = findViewById(R.id.progressContainer)

        btnIncrease.setOnClickListener {
            if (currentProgress < maxProgress) {
                currentProgress++
                val progressWidth = (progressContainer.width * (currentProgress / maxProgress.toFloat())).toInt()

                // Animate progress bar increase
                progressView.layoutParams.width = progressWidth
                progressView.requestLayout()

                // Optionally, show the current progress as a Toast
                Toast.makeText(this, "Progress: $currentProgress%", Toast.LENGTH_SHORT).show()
            }
        }

    }




}