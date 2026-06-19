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
        val wordList = WordRepository.loadWords(this)
        // currentQuestion (1-indexed) をリストのインデックス (0-indexed) に変換。
        // リスト範囲外の場合はループさせる。
        val currentWord = if (wordList.isNotEmpty()) {
            wordList[(currentQuestion - 1) % wordList.size]
        } else {
            Word(1, "爱", "ài", "愛する")
        }
        val correctPinyin = currentWord.pinyin

        // 選択肢ボタンのテキスト設定（仮実装：正解と、適当なバリエーション）
        // ※ 本来はここでバリエーション生成ロジックを入れるが、今回は要件通り「正解が含まれる」ように簡易設定
        findViewById<Button>(R.id.btnOption1).text = correctPinyin
        findViewById<Button>(R.id.btnOption2).text = correctPinyin + "1"
        findViewById<Button>(R.id.btnOption3).text = correctPinyin + "2"
        if (optionsCount == 5) {
            btnOption4.text = correctPinyin + "3"
            btnOption5.text = correctPinyin + "4"
        }

        // 選択肢ボタンのクリックリスナー設定
        val optionButtons = listOf(
            findViewById(R.id.btnOption1),
            findViewById(R.id.btnOption2),
            findViewById(R.id.btnOption3),
            btnOption4,
            btnOption5,
        )

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