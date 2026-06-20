package com.pinyineartraining.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.result_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 10)
        val correctCount = intent.getIntExtra("CORRECT_COUNT", 0)

        // スコア計算
        val score = correctCount * 100
        val accuracy = if (totalQuestions > 0) (correctCount.toDouble() / totalQuestions * 100).toInt() else 0

        // スコアの保存（初回表示時のみ）
        if (savedInstanceState == null) {
            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            val record = ScoreRecord(
                score = score,
                correctCount = correctCount,
                totalQuestions = totalQuestions,
                accuracy = accuracy,
                achievedAt = dateFormat.format(Date())
            )
            ScoreRepository.saveScore(this, record)
        }

        val tvCorrectCount = findViewById<TextView>(R.id.tvCorrectCount)
        val tvAccuracy = findViewById<TextView>(R.id.tvAccuracy)
        val tvScore = findViewById<TextView>(R.id.tvScore)
        val btnHome = findViewById<Button>(R.id.btnHome)

        tvCorrectCount.text = getString(R.string.result_correct_count_format, correctCount, totalQuestions)
        tvAccuracy.text = getString(R.string.result_accuracy_format, accuracy)
        tvScore.text = getString(R.string.result_score_format, score)

        btnHome.setOnClickListener {
            // MainActivityへ戻り、スタックをクリアする
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }
}