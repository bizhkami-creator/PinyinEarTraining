package com.pinyineartraining.app

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

data class Word(
    val level: Int,
    val hanzi: String,
    val pinyin: String,
    val meaning: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(level)
        parcel.writeString(hanzi)
        parcel.writeString(pinyin)
        parcel.writeString(meaning)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Word> {
        override fun createFromParcel(parcel: Parcel): Word = Word(parcel)
        override fun newArray(size: Int): Array<Word?> = arrayOfNulls(size)
    }
}

object WordRepository {
    private const val TAG = "WordRepository"
    private const val CSV_FILE = "hsk1_words.csv"

    fun loadWords(context: Context): List<Word> {
        val wordList = mutableListOf<Word>()
        try {
            val inputStream = context.assets.open(CSV_FILE)
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            // Skip header
            reader.readLine()
            
            var line: String? = reader.readLine()
            while (line != null) {
                val tokens = line.split(",")
                if (tokens.size >= 4) {
                    val level = tokens[0].toIntOrNull() ?: 1
                    val hanzi = tokens[1]
                    val pinyin = tokens[2]
                    val meaning = tokens[3]
                    wordList.add(Word(level, hanzi, pinyin, meaning))
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading CSV: ${e.message}")
            // Fallback to minimal data if CSV loading fails
            if (wordList.isEmpty()) {
                wordList.add(Word(1, "爱", "ài", "愛する"))
            }
        }
        return wordList
    }
}

object WordData {
    // Deprecated: use WordRepository.loadWords instead
    val hsk1SampleList = listOf(
        Word(1, "爱", "ài", "愛する"),
        Word(1, "八", "bā", "8"),
        Word(1, "爸爸", "bàba", "お父さん"),
        Word(1, "杯子", "bēizi", "コップ"),
        Word(1, "北京", "Běijīng", "北京"),
        Word(1, "本", "běn", "〜冊（本を数える）"),
        Word(1, "不客气", "bú kèqi", "どういたしまして"),
        Word(1, "菜", "cài", "料理、野菜"),
        Word(1, "茶", "chá", "お茶"),
        Word(1, "吃", "chí", "食べる")
    )
}