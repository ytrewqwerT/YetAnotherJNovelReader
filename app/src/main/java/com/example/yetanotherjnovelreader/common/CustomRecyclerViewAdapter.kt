package com.example.yetanotherjnovelreader.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.data.RemoteRepository
import kotlinx.android.synthetic.main.recycler_item.view.*

class CustomRecyclerViewAdapter<T : ListItem>
    : RecyclerView.Adapter<CustomRecyclerViewAdapter<T>.ViewHolder>() {

    private var items: List<T> = emptyList()

    fun setItems(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contents = items[position].getListItemContents()

        holder.titleText.text = contents.mTitle
        holder.subText.text = contents.mText
        val repository = RemoteRepository.getInstance(holder.imageView.context)
        holder.imageView.setImageUrl(contents.mImageUrl, repository.imageLoader)
    }
    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.title
        val subText: TextView = view.subText
        val imageView: NetworkImageView = view.image
    }
}