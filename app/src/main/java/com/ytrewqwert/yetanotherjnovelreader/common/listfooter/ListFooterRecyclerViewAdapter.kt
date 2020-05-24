package com.ytrewqwert.yetanotherjnovelreader.common.listfooter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R

class ListFooterRecyclerViewAdapter(
    private val listener: ListFooter.InteractionListener? = null
) : RecyclerView.Adapter<ListFooterRecyclerViewAdapter.ViewHolder>() {

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