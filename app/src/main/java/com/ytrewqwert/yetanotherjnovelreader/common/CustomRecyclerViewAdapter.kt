package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.android.synthetic.main.recycler_item.view.*

class CustomRecyclerViewAdapter(
    private val listener: ListItem.InteractionListener? = null
) : RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder>() {

    private var items: List<ListItem> = emptyList()

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contents = items[position].getListItemContents()

        holder.titleText.text = contents.mTitle
        holder.subText.text = contents.mText

        val repository = Repository.getInstance(holder.imageView.context)
        repository.getImage(contents.mImageUrl) { holder.imageView.setImageBitmap(it) }

        if (contents.progress != null) {
            val percentage = (100 * contents.progress).toInt()
            holder.progressBar.progress = percentage
            holder.progressBar.visibility = View.VISIBLE
        } else {
            holder.progressBar.visibility = View.INVISIBLE
        }

        if (contents.clickable) {
            holder.view.foreground = null
            holder.view.setOnClickListener { listener?.onClick(items[position]) }
        } else {
            val disabledColor = holder.view.resources.getColor(R.color.disabled, null)
            holder.view.foreground = ColorDrawable(disabledColor)
            holder.view.isClickable = false
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    fun setItems(newItems: List<ListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.title
        val subText: TextView = view.subText
        val imageView: ImageView = view.image
        val progressBar: ProgressBar = view.progress
    }
}