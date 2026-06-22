package com.pinyineartraining.app

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class QuizActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var currentWord: Word? = null
    private var isTtsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.quiz_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // TTSの初期化
        tts = TextToSpeech(this, this)

        // Intentから設定を取得
        val mode = intent.getStringExtra("MODE") ?: "normal"
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
        if (mode == "review") {
            tvProgress.append(" (復習)")
        }

        // 選択肢数に応じてボタンの表示/非表示を切り替え
        if (optionsCount == 3) {
            btnOption4.visibility = View.GONE
            btnOption5.visibility = View.GONE
        }

        // 単語データの取得
        currentWord = if (wordList.isNotEmpty()) {
            wordList[(currentQuestion - 1) % wordList.size]
        } else {
            Word(1, "爱", "ài", "愛する")
        }
        val correctPinyin = currentWord?.pinyin ?: ""

        // 選択肢の生成
        val allVariations = PinyinUtils.generateToneVariations(correctPinyin)
        val choices = if (optionsCount == 3) {
            val otherVariations = allVariations.filter { it != correctPinyin }.shuffled()
            (listOf(correctPinyin) + otherVariations.take(2)).shuffled()
        } else {
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
                    putExtra("MODE", mode)
                    putExtra("IS_CORRECT", selectedPinyin == correctPinyin)
                    putExtra("SELECTED_PINYIN", selectedPinyin)
                    putExtra("TOTAL_QUESTIONS", totalQuestions)
                    putExtra("OPTIONS_COUNT", optionsCount)
                    putExtra("CURRENT_QUESTION", currentQuestion)
                    putExtra("CORRECT_COUNT", correctCount)
                    putExtra("WORD_LIST", wordList)
                    putExtra("WORD_DATA", currentWord)
                }
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            speakCurrentWord()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.CHINESE)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported")
                Toast.makeText(this, "中国語の音声データが見つかりません", Toast.LENGTH_SHORT).show()
            } else {
                isTtsReady = true
                // 初回自動再生
                speakCurrentWord()
            }
        } else {
            Log.e("TTS", "Initialization failed")
            Toast.makeText(this, "TTSの初期化に失敗しました", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakCurrentWord() {
        if (isTtsReady) {
            val text = currentWord?.hanzi ?: ""
            if (text.isNotEmpty()) {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}