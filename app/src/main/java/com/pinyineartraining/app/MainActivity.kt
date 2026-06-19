package com.pinyineartraining.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // UI要素の初期化
        val tvHighScore = findViewById<TextView>(R.id.tvHighScore)
        val spinnerQuestions = findViewById<Spinner>(R.id.spinnerQuestions)
        val spinnerOptions = findViewById<Spinner>(R.id.spinnerOptions)
        val btnStart = findViewById<Button>(R.id.btnStart)

        // 最高スコアの表示（仮データ）
        tvHighScore.text = getString(R.string.high_score_format, 1000)

        // STARTボタンのクリックリスナー
        btnStart.setOnClickListener {
            val selectedQuestionsStr = spinnerQuestions.selectedItem.toString()
            val selectedOptionsStr = spinnerOptions.selectedItem.toString()
            
            // 数値部分のみ抽出
            val totalQuestions = selectedQuestionsStr.replace("問", "").toInt()
            val optionsCount = selectedOptionsStr.replace("択", "").toInt()

            // 単語リストを読み込んでランダムに抽出
            val allWords = WordRepository.loadWords(this)
            val shuffledWords = allWords.shuffled()
            val sessionWords = if (shuffledWords.size > totalQuestions) {
                shuffledWords.take(totalQuestions)
            } else {
                shuffledWords
            }
            
            // 実際の出題数を調整（CSVの単語数が選択数より少ない場合）
            val finalTotalQuestions = sessionWords.size

            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("TOTAL_QUESTIONS", finalTotalQuestions)
                putExtra("OPTIONS_COUNT", optionsCount)
                putExtra("CURRENT_QUESTION", 1)
                putExtra("CORRECT_COUNT", 0)
                putParcelableArrayListExtra("WORD_LIST", ArrayList(sessionWords))
            }
            startActivity(intent)
        }
    }
}