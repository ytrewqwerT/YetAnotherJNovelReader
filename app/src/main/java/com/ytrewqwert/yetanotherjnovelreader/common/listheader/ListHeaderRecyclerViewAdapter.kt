package com.ytrewqwert.yetanotherjnovelreader.common.listheader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.ImageSource
import kotlinx.android.synthetic.main.list_header.view.*

/**
 * A [RecyclerView.Adapter] for displaying [ListHeader]s.
 *
 * @property[imageSource] An object from which referenced images can be retrieved.
 */
class ListHeaderRecyclerViewAdapter(
    private val imageSource: ImageSource? = null
) : RecyclerView.Adapter<ListHeaderRecyclerViewAdapter.ViewHolder>() {

    private var items: List<ListHeader> = emptyList()

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contents = items[position].getListHeaderContents()

        holder.imageUrl = contents.mImageUrl
        holder.titleText.text = contents.mTitle
        holder.descText.text = contents.mText

        holder.imageView.setImageDrawable(null)
        if (contents.mImageUrl != null) {
            imageSource?.getImage(contents.mImageUrl) { url, image ->
                if (url == holder.imageUrl) holder.imageView.setImageDrawable(image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_header, parent, false)
        return ViewHolder(view)
    }

    fun setItems(newItems: List<ListHeader>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.image
        val titleText: TextView = view.title
        val descText: TextView = view.description
        var imageUrl: String? = null
    }
}