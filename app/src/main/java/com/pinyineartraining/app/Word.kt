package com.pinyineartraining.app

import android.os.Parcel
import android.os.Parcelable

data class Word(
    val hanzi: String,
    val pinyin: String,
    val meaning: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
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

object WordData {
    val hsk1SampleList = listOf(
        Word("爱", "ài", "愛する"),
        Word("八", "bā", "8"),
        Word("爸爸", "bàba", "お父さん"),
        Word("杯子", "bēizi", "コップ"),
        Word("北京", "Běijīng", "北京"),
        Word("本", "běn", "〜冊（本を数える）"),
        Word("不客气", "bú kèqi", "どういたしまして"),
        Word("菜", "cài", "料理、野菜"),
        Word("茶", "chá", "お茶"),
        Word("吃", "chí", "食べる")
    )
}