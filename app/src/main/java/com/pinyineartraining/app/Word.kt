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

    fun loadWords(context: Context, level: Int): List<Word> {
        val wordList = mutableListOf<Word>()
        val fileName = "hsk${level}_words.csv"
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            // Skip header
            reader.readLine()
            
            var line: String? = reader.readLine()
            while (line != null) {
                val tokens = line.split(",")
                if (tokens.size >= 4) {
                    val levelVal = tokens[0].toIntOrNull() ?: level
                    val hanzi = tokens[1]
                    val pinyin = tokens[2]
                    val meaning = tokens[3]
                    wordList.add(Word(levelVal, hanzi, pinyin, meaning))
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading CSV ($fileName): ${e.message}")
        }
        return wordList
    }

    fun isLevelAvailable(context: Context, level: Int): Boolean {
        return try {
            context.assets.open("hsk${level}_words.csv").close()
            true
        } catch (e: Exception) {
            false
        }
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

object PinyinUtils {
    private val toneVariations = mapOf(
        'a' to listOf("ā", "á", "ǎ", "à", "a"),
        'ā' to listOf("ā", "á", "ǎ", "à", "a"),
        'á' to listOf("ā", "á", "ǎ", "à", "a"),
        'ǎ' to listOf("ā", "á", "ǎ", "à", "a"),
        'à' to listOf("ā", "á", "ǎ", "à", "a"),
        'e' to listOf("ē", "é", "ě", "è", "e"),
        'ē' to listOf("ē", "é", "ě", "è", "e"),
        'é' to listOf("ē", "é", "ě", "è", "e"),
        'ě' to listOf("ē", "é", "ě", "è", "e"),
        'è' to listOf("ē", "é", "ě", "è", "e"),
        'i' to listOf("ī", "í", "ǐ", "ì", "i"),
        'ī' to listOf("ī", "í", "ǐ", "ì", "i"),
        'í' to listOf("ī", "í", "ǐ", "ì", "i"),
        'ǐ' to listOf("ī", "í", "ǐ", "ì", "i"),
        'ì' to listOf("ī", "í", "ǐ", "ì", "i"),
        'o' to listOf("ō", "ó", "ǒ", "ò", "o"),
        'ō' to listOf("ō", "ó", "ǒ", "ò", "o"),
        'ó' to listOf("ō", "ó", "ǒ", "ò", "o"),
        'ǒ' to listOf("ō", "ó", "ǒ", "ò", "o"),
        'ò' to listOf("ō", "ó", "ǒ", "ò", "o"),
        'u' to listOf("ū", "ú", "ǔ", "ù", "u"),
        'ū' to listOf("ū", "ú", "ǔ", "ù", "u"),
        'ú' to listOf("ū", "ú", "ǔ", "ù", "u"),
        'ǔ' to listOf("ū", "ú", "ǔ", "ù", "u"),
        'ù' to listOf("ū", "ú", "ǔ", "ù", "u"),
        'ü' to listOf("ǖ", "ǘ", "ǚ", "ǜ", "ü"),
        'ǖ' to listOf("ǖ", "ǘ", "ǚ", "ǜ", "ü"),
        'ǘ' to listOf("ǖ", "ǘ", "ǚ", "ǜ", "ü"),
        'ǚ' to listOf("ǖ", "ǘ", "ǚ", "ǜ", "ü"),
        'ǜ' to listOf("ǖ", "ǘ", "ǚ", "ǜ", "ü")
    )

    fun generateToneVariations(pinyin: String): List<String> {
        val vowelIndex = pinyin.indexOfFirst { toneVariations.containsKey(it.lowercaseChar()) }
        if (vowelIndex == -1) return listOf(pinyin)

        val targetChar = pinyin[vowelIndex].lowercaseChar()
        val variations = toneVariations[targetChar] ?: return listOf(pinyin)

        return variations.map { variation ->
            val newChar = if (pinyin[vowelIndex].isUpperCase()) variation.uppercase() else variation
            pinyin.substring(0, vowelIndex) + newChar + pinyin.substring(vowelIndex + 1)
        }
    }
}