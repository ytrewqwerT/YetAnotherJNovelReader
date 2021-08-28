package com.ytrewqwert.yetanotherjnovelreader.main.volumepartslist

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListAdapter
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartComparator
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import kotlinx.android.synthetic.main.list_part_title.view.*

/**
 * A SwipeableListAdapter for lists containing parts for a single volume.
 *
 * @param[listener] A handler for events that occur on a part in the list.
 */
class VolumePartsListAdapter(
    private val listener: Listener? = null
) : SwipeableListAdapter<PartFull, VolumePartsListAdapter.ViewHolder>(PartComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_part_title, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PartFull) {
        holder.titleText.text = item.part.title
        val progress = (item.progress?.progress ?: 0.0).toFloat()

        val completedParams = holder.completedProgress.layoutParams as LinearLayout.LayoutParams
        completedParams.weight = progress
        holder.completedProgress.layoutParams = completedParams

        val remainingParams = holder.remainingProgress.layoutParams as LinearLayout.LayoutParams
        remainingParams.weight = 1 - progress
        holder.remainingProgress.layoutParams = remainingParams

        if (item.part.readable()) {
            holder.view.foreground = null
            holder.view.setOnClickListener { listener?.onPartClick(item) }
        } else {
            val disabledColor = holder.view.resources.getColor(R.color.disabled, null)
            holder.view.foreground = ColorDrawable(disabledColor)
            holder.view.isClickable = false
        }
    }

    /** RecyclerView.ViewHolder for the VolumePartsListAdapter. */
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.title
        val completedProgress: View = view.progressCompleted
        val remainingProgress: View = view.progressRemaining
    }

    /** Interface for objects that wish to respond to events acting on items in the list. */
    interface Listener {
        /** Called when a [PartFull] is clicked by the user. */
        fun onPartClick(part: PartFull)
    }
}