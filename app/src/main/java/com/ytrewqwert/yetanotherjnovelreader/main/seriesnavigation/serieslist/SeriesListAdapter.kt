package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serieslist

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
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieComparator
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import kotlinx.android.synthetic.main.list_item_compact.view.*

/**
 * A SwipeableListAdapter for lists containing series.
 *
 * @param[listener] A handler for events that occur on a serie in the list.
 * @param[imageSource] A source for fetching images.
 */
class SeriesListAdapter(
    private val listener: Listener? = null,
    private val imageSource: ImageSource? = null
) : SwipeableListAdapter<SerieFull, SeriesListAdapter.ViewHolder>(SerieComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_compact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SerieFull) {
        holder.titleText.text = item.serie.title
        holder.imageUrl = item.serie.coverUrl

        holder.imageView.setImageDrawable(null)
        imageSource?.getImage(item.serie.coverUrl) { url, image ->
            if (url == holder.imageUrl) holder.imageView.setImageDrawable(image)
        }

        val followIconId = when (item.isFollowed()) {
            true -> R.drawable.ic_star_24dp
            false -> R.drawable.ic_star_border_24dp
        }
        val resources = holder.view.resources
        val followIcon = ResourcesCompat.getDrawable(resources, followIconId, null)
        holder.following.setImageDrawable(followIcon)

        holder.progressBar.visibility = View.INVISIBLE

        holder.following.setOnClickListener { listener?.onSerieFollowClick(item) }
        holder.view.setOnClickListener { listener?.onSerieClick(item) }
    }

    /** RecyclerView.ViewHolder for the SeriesListAdapter. */
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.title
        val imageView: ImageView = view.image
        val progressBar: ProgressBar = view.progress
        val following: ImageView = view.following
        var imageUrl: String? = null
    }

    /** Interface for objects that wish to respond to events acting on items in the list. */
    interface Listener {
        /** Called when a [SerieFull] is clicked by the user. */
        fun onSerieClick(serie: SerieFull)
        /** Called when a [SerieFull]'s 'follow' button is clicked by the user. */
        fun onSerieFollowClick(serie: SerieFull)
    }
}