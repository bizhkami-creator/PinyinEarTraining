package com.pinyineartraining.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ScoreRecord(
    val score: Int,
    val correctCount: Int,
    val totalQuestions: Int,
    val accuracy: Int,
    val achievedAt: String
)

object ScoreRepository {
    private const val PREF_NAME = "pinyin_ear_training_prefs"
    private const val KEY_SCORES = "key_scores"
    private val gson = Gson()

    fun saveScore(context: Context, record: ScoreRecord) {
        val scores = loadScores(context).toMutableList()
        scores.add(record)
        // スコア降順でソート
        scores.sortByDescending { it.score }
        // TOP 10のみ保持
        val top10 = if (scores.size > 10) scores.take(10) else scores
        
        val json = gson.toJson(top10)
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SCORES, json)
            .apply()
    }

    fun loadScores(context: Context): List<ScoreRecord> {
        val json = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SCORES, null) ?: return emptyList()
        
        val type = object : TypeToken<List<ScoreRecord>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun getHighScore(context: Context): Int {
        val scores = loadScores(context)
        return if (scores.isNotEmpty()) scores[0].score else 0
    }
}