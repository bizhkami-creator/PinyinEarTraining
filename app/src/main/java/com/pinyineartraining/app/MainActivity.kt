package com.pinyineartraining.app

import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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
            val selectedQuestions = spinnerQuestions.selectedItem.toString()
            val selectedOptions = spinnerOptions.selectedItem.toString()
            
            val message = "設定: $selectedQuestions / $selectedOptions で開始します"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}