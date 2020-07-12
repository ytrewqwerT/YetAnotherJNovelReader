package com.ytrewqwert.yetanotherjnovelreader.common.listitem

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.ImageSource
import kotlinx.android.synthetic.main.list_item.view.*

/**
 * A [RecyclerView.Adapter] for displaying [ListItem]s.
 *
 * @property[imageSource] An object from which referenced images can be retrieved.
 */
class ListItemRecyclerViewAdapter(
    private val listener: ListItem.InteractionListener? = null,
    private val imageSource: ImageSource? = null
) : RecyclerView.Adapter<ListItemRecyclerViewAdapter.ViewHolder>() {

    var items: List<ListItem> = emptyList()
        set(value) { field = value; notifyDataSetChanged() }
    private val filteredItems: List<ListItem> get() = items.filter { it.hasTerm(filterStr) }

    var filterStr: String = ""
        set(value) { field = value; notifyDataSetChanged() }

    override fun getItemCount(): Int = filteredItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contents = filteredItems[position].getListItemContents()

        holder.titleText.text = contents.mTitle
        holder.subText.text = contents.mText
        holder.imageUrl = contents.mImageUrl

        holder.imageView.setImageDrawable(null)
        if (contents.mImageUrl != null) {
            imageSource?.getImage(contents.mImageUrl) { url, image ->
                if (url == holder.imageUrl) holder.imageView.setImageDrawable(image)
            }
        }

        val followIconId = when (contents.isFollowing) {
            true -> R.drawable.ic_star_24dp
            false -> R.drawable.ic_star_border_24dp
        }
        val followIcon = holder.view.resources.getDrawable(followIconId, null)
        holder.following.setImageDrawable(followIcon)

        if (contents.progress != null) {
            val percentage = (100 * contents.progress).toInt()
            holder.progressBar.progress = percentage
            holder.progressBar.visibility = View.VISIBLE
        } else {
            holder.progressBar.visibility = View.INVISIBLE
        }

        holder.following.setOnClickListener { listener?.onFollowClick(filteredItems[position]) }
        if (contents.clickable) {
            holder.view.foreground = null
            holder.view.setOnClickListener { listener?.onClick(filteredItems[position]) }
        } else {
            val disabledColor = holder.view.resources.getColor(R.color.disabled, null)
            holder.view.foreground = ColorDrawable(disabledColor)
            holder.view.isClickable = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.title
        val subText: TextView = view.subText
        val imageView: ImageView = view.image
        val progressBar: ProgressBar = view.progress
        val following: ImageView = view.following
        var imageUrl: String? = null
    }
}