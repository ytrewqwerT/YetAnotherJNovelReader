package com.ytrewqwert.yetanotherjnovelreader.main.partslists

import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.ImageSource
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListAdapter
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartComparator
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.main.MainViewModel
import kotlinx.android.synthetic.main.list_item_compact.view.*

/**
 * A SwipeableListAdapter for lists containing parts mixed across series/volumes.
 *
 * @param[listener] A handler for events that occur on a part in the list.
 * @param[imageSource] A source for fetching images.
 */
class PartsListAdapter(
    private val listener: Listener? = null,
    private val imageSource: ImageSource? = null
) : SwipeableListAdapter<PartFull, PartsListAdapter.ViewHolder>(PartComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_compact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PartFull) {
        holder.part = item

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

    /** RecyclerView.ViewHolder for the PartsListAdapter. */
    inner class ViewHolder(val view: View)
        : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        val titleText: TextView = view.title
        val imageView: ImageView = view.image
        val progressBar: ProgressBar = view.progress
        val following: ImageView = view.following
        var imageUrl: String? = null

        var part: PartFull? = null

        init { view.setOnCreateContextMenuListener(this) }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            part?.let { part ->
                val isRead = part.progress?.progress == 1.0
                val readText = if (isRead) "Mark As Unread" else "Mark As Read"

                menu.add(Menu.NONE, MainViewModel.CONTEXT_TO_VOLUME, Menu.NONE, "To Volume")
                menu.add(Menu.NONE, MainViewModel.CONTEXT_TO_SERIES, Menu.NONE, "To Series")
                menu.add(Menu.NONE, MainViewModel.CONTEXT_TOGGLE_READ, Menu.NONE, readText)

                listener?.onContextMenuCreatedForPart(part)
            }
        }
    }

    /** Interface for objects that wish to respond to events acting on items in the list. */
    interface Listener {
        /** Called when a [PartFull] is clicked by the user. */
        fun onPartClick(part: PartFull)
        /** Called when a [PartFull]'s 'follow' button is clicked by the user. */
        fun onPartFollowClick(part: PartFull)
        /** Called when a context menu for [part] is created. */
        fun onContextMenuCreatedForPart(part: PartFull)
    }
}