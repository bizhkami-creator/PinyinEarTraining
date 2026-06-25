package com.pinyineartraining.app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WeakWordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weak_words)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.weak_words_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rvWeakWords = findViewById<RecyclerView>(R.id.rvWeakWords)
        val tvEmptyMessage = findViewById<TextView>(R.id.tvEmptyMessage)
        
        val selectedLevel = intent.getIntExtra("LEVEL", 1)

        val weakWords = WeakWordRepository.loadWeakWordsByLevel(this, selectedLevel)
            .sortedByDescending { it.mistakeCount }

        if (weakWords.isEmpty()) {
            rvWeakWords.visibility = View.GONE
            tvEmptyMessage.visibility = View.VISIBLE
        } else {
            rvWeakWords.visibility = View.VISIBLE
            tvEmptyMessage.visibility = View.GONE
            rvWeakWords.layoutManager = LinearLayoutManager(this)
            rvWeakWords.adapter = WeakWordAdapter(weakWords)
        }
    }
}