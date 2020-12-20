package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serievolumes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.ImageSource
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListAdapter
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import kotlinx.android.synthetic.main.list_volume.view.*

class ListVolumeRecyclerViewAdapter(
    private val listener: ClickListener? = null,
    private val imageSource: ImageSource? = null
) : SwipeableListAdapter<VolumeFull, ListVolumeRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_volume, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: VolumeFull) {
        val volume = item.volume
        val parts = item.parts

        holder.titleText.text = volume.title
        holder.imageUrl = volume.coverUrl

        holder.imageView.setImageDrawable(null)
        imageSource?.getImage(volume.coverUrl) { url, image ->
            if (url == holder.imageUrl) holder.imageView.setImageDrawable(image)
        }

        if (parts.isNotEmpty()) {
            val orderedProgress = parts
                .sortedBy { it.part.seriesPartNum }
                .map { it.progress?.progress ?: 0.0 }
            holder.progressManager.setProgress(orderedProgress)
            holder.progressBar.visibility = View.VISIBLE
        } else {
            holder.progressManager.setProgress(null)
            holder.progressBar.visibility = View.INVISIBLE
        }

        holder.view.setOnClickListener { listener?.onVolumeClick(item) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.title
        val imageView: ImageView = view.image
        val progressBar: LinearLayout = view.progress
        var imageUrl: String? = null
        val progressManager = SegmentedProgressViewManager().apply { setLayout(progressBar) }
    }

    interface ClickListener {
        /** Called when a [VolumeFull] is clicked by the user. */
        fun onVolumeClick(volume: VolumeFull)
    }
}