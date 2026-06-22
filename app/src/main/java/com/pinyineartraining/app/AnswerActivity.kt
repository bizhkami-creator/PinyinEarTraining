package com.pinyineartraining.app

import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AnswerActivity : AppCompatActivity() {

    private var toneGenerator: ToneGenerator? = null

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
        val mode = intent.getStringExtra("MODE") ?: "normal"
        val selectedPinyin = intent.getStringExtra("SELECTED_PINYIN") ?: ""
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 10)
        val optionsCount = intent.getIntExtra("OPTIONS_COUNT", 3)
        val currentQuestion = intent.getIntExtra("CURRENT_QUESTION", 1)
        var correctCount = intent.getIntExtra("CORRECT_COUNT", 0)
        
        @Suppress("DEPRECATION")
        val wordList = intent.getParcelableArrayListExtra<Word>("WORD_LIST") ?: arrayListOf()

        // IntentからWordオブジェクトを受け取る（SDK 33以降推奨の型安全な取得も考慮しつつ、今回は互換性の高い方法で取得）
        @Suppress("DEPRECATION")
        val currentWord = intent.getParcelableExtra<Word>("WORD_DATA") ?: Word(1, "爱", "ài", "愛する")

        // 設定の読み込み
        val prefs = getSharedPreferences("pinyin_ear_training_prefs", MODE_PRIVATE)
        val soundEnabled = prefs.getBoolean("key_sound_enabled", true)

        if (soundEnabled) {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            if (isCorrect) {
                // 正解音: 「ピンポーン！」（高音150ms -> 低音400ms）
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_3, 150)
                Handler(Looper.getMainLooper()).postDelayed({
                    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_1, 400)
                }, 150)
            } else {
                // 不正解音: 「ブブー！」（低い音を長めに）
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 400)
            }
        }

        val tvResult = findViewById<TextView>(R.id.tvResult)
        val tvUserAnswer = findViewById<TextView>(R.id.tvUserAnswer)
        val tvCorrectPinyin = findViewById<TextView>(R.id.tvCorrectPinyin)
        val tvOvercome = findViewById<TextView>(R.id.tvOvercome)
        val tvHanzi = findViewById<TextView>(R.id.tvHanzi)
        val tvPinyin = findViewById<TextView>(R.id.tvPinyin)
        val tvMeaning = findViewById<TextView>(R.id.tvMeaning)
        val btnNext = findViewById<Button>(R.id.btnNext)

        if (isCorrect) {
            correctCount++
            // 正解の場合：復習モードならミス回数を減らす
            if (mode == "review" && savedInstanceState == null) {
                val removed = WeakWordRepository.reduceMistake(this, currentWord)
                if (removed) {
                    tvOvercome.visibility = View.VISIBLE
                }
            }
        } else {
            // 不正解の場合：苦手単語として保存/更新（初回表示時のみ）
            if (savedInstanceState == null) {
                WeakWordRepository.addMistake(this, currentWord)
            }
        }
        
        // 単語データ
        val correctPinyin = currentWord.pinyin
        val hanzi = currentWord.hanzi
        val meaning = currentWord.meaning

        if (isCorrect) {
            tvResult.text = getString(R.string.answer_correct)
            tvResult.setTextColor(Color.parseColor("#4CAF50")) // Green
            tvUserAnswer.visibility = View.GONE
            tvCorrectPinyin.visibility = View.GONE
        } else {
            tvResult.text = getString(R.string.answer_incorrect)
            tvResult.setTextColor(Color.parseColor("#F44336")) // Red
            tvUserAnswer.text = getString(R.string.answer_user_format, selectedPinyin)
            tvUserAnswer.visibility = View.VISIBLE
            tvCorrectPinyin.text = getString(R.string.answer_correct_pinyin_format, correctPinyin)
            tvCorrectPinyin.visibility = View.VISIBLE
            tvOvercome.visibility = View.GONE
        }

        tvHanzi.text = hanzi
        tvPinyin.text = correctPinyin
        tvMeaning.text = meaning

        btnNext.setOnClickListener {
            if (currentQuestion < totalQuestions) {
                // 次の問題へ
                val intent = Intent(this, QuizActivity::class.java).apply {
                    putExtra("MODE", mode)
                    putExtra("TOTAL_QUESTIONS", totalQuestions)
                    putExtra("OPTIONS_COUNT", optionsCount)
                    putExtra("CURRENT_QUESTION", currentQuestion + 1)
                    putExtra("CORRECT_COUNT", correctCount)
                    putExtra("WORD_LIST", wordList)
                    // バックスタックが残りすぎないように、前のクイズ/回答画面はクリア対象
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
            } else {
                // 全問題終了、結果画面へ
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("MODE", mode)
                    putExtra("TOTAL_QUESTIONS", totalQuestions)
                    putExtra("CORRECT_COUNT", correctCount)
                }
                startActivity(intent)
            }
            finish()
        }
    }

    override fun onDestroy() {
        toneGenerator?.release()
        super.onDestroy()
    }
}