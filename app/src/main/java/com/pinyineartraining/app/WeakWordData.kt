package com.pinyineartraining.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class WeakWord(
    val level: Int,
    val hanzi: String,
    val pinyin: String,
    val meaning: String,
    var mistakeCount: Int
)

object WeakWordRepository {
    private const val PREF_NAME = "pinyin_ear_training_prefs"
    private const val KEY_WEAK_WORDS = "key_weak_words"
    private val gson = Gson()

    fun addMistake(context: Context, word: Word) {
        val weakWords = loadWeakWords(context).toMutableList()
        val existing = weakWords.find { it.hanzi == word.hanzi && it.pinyin == word.pinyin }
        
        if (existing != null) {
            existing.mistakeCount++
        } else {
            weakWords.add(WeakWord(
                level = word.level,
                hanzi = word.hanzi,
                pinyin = word.pinyin,
                meaning = word.meaning,
                mistakeCount = 1
            ))
        }

        val json = gson.toJson(weakWords)
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_WEAK_WORDS, json)
            .apply()
    }

    fun loadWeakWords(context: Context): List<WeakWord> {
        val json = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_WEAK_WORDS, null) ?: return emptyList()
        
        val type = object : TypeToken<List<WeakWord>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun getWeakWordCount(context: Context): Int {
        return loadWeakWords(context).size
    }
}
