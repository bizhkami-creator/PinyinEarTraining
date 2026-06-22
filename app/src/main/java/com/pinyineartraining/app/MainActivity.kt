package com.pinyineartraining.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
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
        val spinnerQuestions = findViewById<Spinner>(R.id.spinnerQuestions)
        val spinnerOptions = findViewById<Spinner>(R.id.spinnerOptions)
        val switchSound = findViewById<SwitchCompat>(R.id.switchSound)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnReview = findViewById<Button>(R.id.btnReview)

        // 設定の読み込み
        val prefs = getSharedPreferences("pinyin_ear_training_prefs", MODE_PRIVATE)
        switchSound.isChecked = prefs.getBoolean("key_sound_enabled", true)

        switchSound.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("key_sound_enabled", isChecked).apply()
        }

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
                putExtra("MODE", "normal")
                putExtra("TOTAL_QUESTIONS", finalTotalQuestions)
                putExtra("OPTIONS_COUNT", optionsCount)
                putExtra("CURRENT_QUESTION", 1)
                putExtra("CORRECT_COUNT", 0)
                putParcelableArrayListExtra("WORD_LIST", ArrayList(sessionWords))
            }
            startActivity(intent)
        }

        // 苦手単語復習ボタンのクリックリスナー
        btnReview.setOnClickListener {
            val weakWords = WeakWordRepository.loadWeakWords(this)
            if (weakWords.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_no_weak_words), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedOptionsStr = spinnerOptions.selectedItem.toString()
            val optionsCount = selectedOptionsStr.replace("択", "").toInt()

            // WeakWordをWordに変換してシャッフル
            val sessionWords = weakWords.map { 
                Word(it.level, it.hanzi, it.pinyin, it.meaning) 
            }.shuffled()

            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("MODE", "review")
                putExtra("TOTAL_QUESTIONS", sessionWords.size)
                putExtra("OPTIONS_COUNT", optionsCount)
                putExtra("CURRENT_QUESTION", 1)
                putExtra("CORRECT_COUNT", 0)
                putParcelableArrayListExtra("WORD_LIST", ArrayList(sessionWords))
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshRanking()
    }

    private fun refreshRanking() {
        val tvHighScore = findViewById<TextView>(R.id.tvHighScore)
        val tvWeakWordCount = findViewById<TextView>(R.id.tvWeakWordCount)
        val rankingContainer = findViewById<LinearLayout>(R.id.rankingContainer)

        // 最高スコアの表示
        val highScore = ScoreRepository.getHighScore(this)
        tvHighScore.text = getString(R.string.high_score_format, highScore)

        // 苦手単語数の表示
        val weakWordCount = WeakWordRepository.getWeakWordCount(this)
        tvWeakWordCount.text = getString(R.string.weak_word_count_format, weakWordCount)

        // ランキングの表示
        val scores = ScoreRepository.loadScores(this)
        rankingContainer.removeAllViews()

        if (scores.isEmpty()) {
            val tv = TextView(this).apply {
                text = getString(R.string.ranking_empty)
                textSize = 14f
            }
            rankingContainer.addView(tv)
        } else {
            scores.forEachIndexed { index, record ->
                val medal = when (index) {
                    0 -> "🥇 "
                    1 -> "🥈 "
                    2 -> "🥉 "
                    else -> "${index + 1}. "
                }
                val tv = TextView(this).apply {
                    text = "$medal${record.score}点 ${record.achievedAt}"
                    textSize = 14f
                    setPadding(0, 0, 0, if (index < scores.size - 1) 4 else 0)
                }
                rankingContainer.addView(tv)
            }
        }
    }
}