package com.ytrewqwert.yetanotherjnovelreader.main.partslists

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.ImageSource
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListAdapter
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import kotlinx.android.synthetic.main.list_item.view.*

class PartsListRecyclerViewAdapter(
    private val listener: ClickListener? = null,
    private val imageSource: ImageSource? = null
) : SwipeableListAdapter<PartFull, PartsListRecyclerViewAdapter.ViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_compact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PartFull) {

        holder.titleText.text = item.part.title
        holder.imageUrl = item.part.coverUrl

        holder.imageView.setImageDrawable(null)
        imageSource?.getImage(item.part.coverUrl) { url, image ->
            if (url == holder.imageUrl) holder.imageView.setImageDrawable(image)
        }

        val followIconId = when (item.isFollowed()) {
            true -> R.drawable.ic_star_24dp
            false -> R.drawable.ic_star_border_24dp
        }
        val resources = holder.view.resources
        val followIcon = ResourcesCompat.getDrawable(resources, followIconId, null)
        holder.following.setImageDrawable(followIcon)

        if (item.progress != null) {
            val percentage = (100 * item.progress.progress).toInt()
            holder.progressBar.progress = percentage
            holder.progressBar.visibility = View.VISIBLE
        } else {
            holder.progressBar.visibility = View.INVISIBLE
        }

        holder.following.setOnClickListener { listener?.onPartFollowClick(item) }
        if (item.part.readable()) {
            holder.view.foreground = null
            holder.view.setOnClickListener { listener?.onPartClick(item) }
        } else {
            val disabledColor = holder.view.resources.getColor(R.color.disabled, null)
            holder.view.foreground = ColorDrawable(disabledColor)
            holder.view.isClickable = false
        }
    }
    
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.title
        val imageView: ImageView = view.image
        val progressBar: ProgressBar = view.progress
        val following: ImageView = view.following
        var imageUrl: String? = null
    }
    
    interface ClickListener {
        /**
         * Called when a [PartFull] is clicked by the user.
         *
         * @param[part] The [PartFull] that was clicked.
         */
        fun onPartClick(part: PartFull)

        /**
         * Called when a [PartFull]'s 'follow' button is clicked by the user.
         *
         * @param[part] The [PartFull] that had it's 'follow' button clicked.
         */
        fun onPartFollowClick(part: PartFull)
    }
}