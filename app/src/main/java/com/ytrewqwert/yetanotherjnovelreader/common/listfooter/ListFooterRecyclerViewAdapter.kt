package com.ytrewqwert.yetanotherjnovelreader.common.listfooter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R

/**
 * A [RecyclerView.Adapter] exposing 0 or 1 items containing a loading indicator, for use as the
 * last adapter in a [MergeAdapter] to signal to a [listener] (via the
 * [ListFooter.InteractionListener.onFooterReached] method), when the user scrolls near the end of
 * the MergeAdapter.
 */
class ListFooterRecyclerViewAdapter(
    private val listener: ListFooter.InteractionListener? = null
) : RecyclerView.Adapter<ListFooterRecyclerViewAdapter.ViewHolder>() {
    /**
     * Set's whether to show the loading item. If set to false, then the [listener] will not be
     * notified when the end of the containing [ConcatAdapter] has been reached.
     */
    var isVisible = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = if (isVisible) 1 else 0
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { listener?.onFooterReached() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_footer, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}