package com.pinyineartraining.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class QuizActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.quiz_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Intentから設定を取得
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 10)
        val optionsCount = intent.getIntExtra("OPTIONS_COUNT", 3)

        // UI要素の初期化
        val tvProgress = findViewById<TextView>(R.id.tvProgress)
        val btnOption4 = findViewById<Button>(R.id.btnOption4)
        val btnOption5 = findViewById<Button>(R.id.btnOption5)

        // プログレス表示の更新 (1問目固定)
        tvProgress.text = getString(R.string.quiz_progress_format, 1, totalQuestions)

        // 選択肢数に応じてボタンの表示/非表示を切り替え
        if (optionsCount == 3) {
            btnOption4.visibility = View.GONE
            btnOption5.visibility = View.GONE
        }

        // 選択肢ボタンのクリックリスナー設定
        val optionButtons = listOf(
            findViewById(R.id.btnOption1),
            findViewById(R.id.btnOption2),
            findViewById(R.id.btnOption3),
            btnOption4,
            btnOption5,
        )

        val correctPinyin = "ài"

        optionButtons.forEach { button ->
            button.setOnClickListener {
                val selectedPinyin = (it as Button).text.toString()
                
                val intent = Intent(this, AnswerActivity::class.java).apply {
                    putExtra("IS_CORRECT", selectedPinyin == correctPinyin)
                    putExtra("SELECTED_PINYIN", selectedPinyin)
                }
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            Toast.makeText(this, "音声再生（モック）", Toast.LENGTH_SHORT).show()
        }
    }
}