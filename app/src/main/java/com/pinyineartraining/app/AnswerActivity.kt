package com.pinyineartraining.app

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AnswerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_answer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.answer_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val isCorrect = intent.getBooleanExtra("IS_CORRECT", false)
        val selectedPinyin = intent.getStringExtra("SELECTED_PINYIN") ?: ""
        
        // 仮の正解データ
        val correctPinyin = "ài"
        val hanzi = "爱"
        val meaning = "愛する"

        val tvResult = findViewById<TextView>(R.id.tvResult)
        val tvCorrectPinyin = findViewById<TextView>(R.id.tvCorrectPinyin)
        val tvHanzi = findViewById<TextView>(R.id.tvHanzi)
        val tvPinyin = findViewById<TextView>(R.id.tvPinyin)
        val tvMeaning = findViewById<TextView>(R.id.tvMeaning)
        val btnNext = findViewById<Button>(R.id.btnNext)

        if (isCorrect) {
            tvResult.text = getString(R.string.answer_correct)
            tvResult.setTextColor(Color.parseColor("#4CAF50")) // Green
            tvCorrectPinyin.visibility = View.GONE
        } else {
            tvResult.text = getString(R.string.answer_incorrect)
            tvResult.setTextColor(Color.parseColor("#F44336")) // Red
            tvCorrectPinyin.text = getString(R.string.answer_correct_pinyin_format, correctPinyin)
            tvCorrectPinyin.visibility = View.VISIBLE
        }

        tvHanzi.text = hanzi
        tvPinyin.text = correctPinyin
        tvMeaning.text = meaning

        btnNext.setOnClickListener {
            // QuizActivityに戻る
            finish()
        }
    }
}