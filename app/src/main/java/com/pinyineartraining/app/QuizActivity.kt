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
        val currentQuestion = intent.getIntExtra("CURRENT_QUESTION", 1)
        val correctCount = intent.getIntExtra("CORRECT_COUNT", 0)
        
        @Suppress("DEPRECATION")
        val wordList = intent.getParcelableArrayListExtra<Word>("WORD_LIST") ?: arrayListOf()

        // UI要素の初期化
        val tvProgress = findViewById<TextView>(R.id.tvProgress)
        val btnOption4 = findViewById<Button>(R.id.btnOption4)
        val btnOption5 = findViewById<Button>(R.id.btnOption5)

        // プログレス表示の更新
        tvProgress.text = getString(R.string.quiz_progress_format, currentQuestion, totalQuestions)

        // 選択肢数に応じてボタンの表示/非表示を切り替え
        if (optionsCount == 3) {
            btnOption4.visibility = View.GONE
            btnOption5.visibility = View.GONE
        }

        // 単語データの取得
        // currentQuestion (1-indexed) をリストのインデックス (0-indexed) に変換。
        val currentWord = if (wordList.isNotEmpty()) {
            wordList[(currentQuestion - 1) % wordList.size]
        } else {
            Word(1, "爱", "ài", "愛する")
        }
        val correctPinyin = currentWord.pinyin

        // 選択肢の生成
        val allVariations = PinyinUtils.generateToneVariations(correctPinyin)
        val choices = if (optionsCount == 3) {
            // 正解を含めて3つ選ぶ
            val otherVariations = allVariations.filter { it != correctPinyin }.shuffled()
            (listOf(correctPinyin) + otherVariations.take(2)).shuffled()
        } else {
            // 5択（すべて、または足りない場合はある分だけ）
            allVariations.shuffled()
        }

        // 選択肢ボタンの設定
        val optionButtons = listOf(
            findViewById<Button>(R.id.btnOption1),
            findViewById<Button>(R.id.btnOption2),
            findViewById<Button>(R.id.btnOption3),
            btnOption4,
            btnOption5,
        )

        // 一旦すべてのボタンを隠してから、必要な分だけ表示
        optionButtons.forEach { it.visibility = View.GONE }

        choices.forEachIndexed { index, choice ->
            if (index < optionButtons.size) {
                optionButtons[index].text = choice
                optionButtons[index].visibility = View.VISIBLE
            }
        }

        optionButtons.forEach { button ->
            button.setOnClickListener {
                val selectedPinyin = (it as Button).text.toString()
                
                val intent = Intent(this, AnswerActivity::class.java).apply {
                    putExtra("IS_CORRECT", selectedPinyin == correctPinyin)
                    putExtra("SELECTED_PINYIN", selectedPinyin)
                    putExtra("TOTAL_QUESTIONS", totalQuestions)
                    putExtra("OPTIONS_COUNT", optionsCount)
                    putExtra("CURRENT_QUESTION", currentQuestion)
                    putExtra("CORRECT_COUNT", correctCount)
                    putExtra("WORD_LIST", wordList)
                    // 単語情報を渡す
                    putExtra("WORD_DATA", currentWord)
                }
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            Toast.makeText(this, "音声再生（モック）", Toast.LENGTH_SHORT).show()
        }
    }
}