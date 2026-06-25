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
        saveWeakWords(context, weakWords)
    }

    /**
     * 苦手単語のミス回数を減らす。0になったらリストから削除する。
     * @return 削除された（克服した）場合はtrueを返す
     */
    fun reduceMistake(context: Context, word: Word): Boolean {
        val weakWords = loadWeakWords(context).toMutableList()
        val existing = weakWords.find { it.hanzi == word.hanzi && it.pinyin == word.pinyin } ?: return false
        
        existing.mistakeCount--
        var removed = false
        if (existing.mistakeCount <= 0) {
            weakWords.remove(existing)
            removed = true
        }
        
        saveWeakWords(context, weakWords)
        return removed
    }

    private fun saveWeakWords(context: Context, weakWords: List<WeakWord>) {
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

    fun loadWeakWordsByLevel(context: Context, level: Int): List<WeakWord> {
        return loadWeakWords(context).filter { it.level == level }
    }

    fun getWeakWordCount(context: Context): Int {
        return loadWeakWords(context).size
    }

    fun getWeakWordCountByLevel(context: Context, level: Int): Int {
        return loadWeakWordsByLevel(context, level).size
    }
}
