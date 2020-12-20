package com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist

import androidx.recyclerview.widget.RecyclerView

abstract class SwipeableListAdapter<T : Any, VH : RecyclerView.ViewHolder>
    : RecyclerView.Adapter<VH>() {

    private var items: List<T> = emptyList()
    final override fun getItemCount(): Int = items.size

    final override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, items[position])
    }

    fun setItems(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

    abstract fun onBindViewHolder(holder: VH, item: T)
}