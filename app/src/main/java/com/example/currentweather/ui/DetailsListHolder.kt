package com.example.currentweather.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.pair_text_view.view.*

class DetailsListHolder(private val pairTextView: View) : RecyclerView.ViewHolder(pairTextView) {

    fun bind(denotation: Int, value: String) {
        pairTextView.denotation.setText(denotation)
        pairTextView.value.text = value
    }

}
