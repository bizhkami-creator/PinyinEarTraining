package com.pinyineartraining.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeakWordAdapter(private val weakWords: List<WeakWord>) :
    RecyclerView.Adapter<WeakWordAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHanzi: TextView = view.findViewById(R.id.itemHanzi)
        val tvPinyin: TextView = view.findViewById(R.id.itemPinyin)
        val tvMeaning: TextView = view.findViewById(R.id.itemMeaning)
        val tvMistakeCount: TextView = view.findViewById(R.id.itemMistakeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weak_word, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = weakWords[position]
        holder.tvHanzi.text = word.hanzi
        holder.tvPinyin.text = word.pinyin
        holder.tvMeaning.text = word.meaning
        holder.tvMistakeCount.text = holder.itemView.context.getString(
            R.string.label_mistake_count, word.mistakeCount
        )
    }

    override fun getItemCount(): Int = weakWords.size
}