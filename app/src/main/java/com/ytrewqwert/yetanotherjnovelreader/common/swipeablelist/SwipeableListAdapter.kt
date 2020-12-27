package com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist

import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView.Adapter companion for a SwipeableListFragment.
 *
 * @param[T] The type to be passed to each ViewHolder in the list.
 * @param[VH] The type of the list's ViewHolder
 */
abstract class SwipeableListAdapter<T : Any, VH : RecyclerView.ViewHolder>
    : RecyclerView.Adapter<VH>() {

    private var items: List<T> = emptyList()
    final override fun getItemCount(): Int = items.size

    final override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, items[position])
    }

    /** Sets the items to be shown in the list. */
    fun setItems(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

    /** Binds the [item] to the [holder] */
    abstract fun onBindViewHolder(holder: VH, item: T)
}