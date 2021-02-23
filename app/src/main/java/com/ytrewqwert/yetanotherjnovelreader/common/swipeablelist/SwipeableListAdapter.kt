package com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * RecyclerView.Adapter companion for a SwipeableListFragment.
 *
 * @param[T] The type to be passed to each ViewHolder in the list.
 * @param[VH] The type of the list's ViewHolder
 */
abstract class SwipeableListAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    private val comparator: DiffUtil.ItemCallback<T>,
    private val workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) : RecyclerView.Adapter<VH>() {

    private var items: List<T> = emptyList()
    final override fun getItemCount(): Int = items.size

    final override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, items[position])
    }

    /** Sets the items to be shown in the list. */
    suspend fun setItems(newItems: List<T>) {
        if (items.isEmpty()) {
            // Avoid notifyItemRangeInserted() on initial list population as that'll cause the UI to
            //  scroll to the bottom of the list when a (visible) footer is visible after this list.
            items = newItems
            notifyDataSetChanged()
            return
        }
        val diff = withContext(workerDispatcher) {
            DiffUtil.calculateDiff(DiffCallback(items, newItems))
        }
        items = newItems
        diff.dispatchUpdatesTo(this)
    }

    /** Binds the [item] to the [holder] */
    abstract fun onBindViewHolder(holder: VH, item: T)

    private inner class DiffCallback(
        private val oldItems: List<T>, private val newItems: List<T>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size
        override fun getNewListSize(): Int = newItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            comparator.areItemsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            comparator.areContentsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? =
            comparator.getChangePayload(oldItems[oldItemPosition], newItems[newItemPosition])
    }
}