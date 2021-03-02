package com.ytrewqwert.yetanotherjnovelreader.common.listheader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.ImageSource
import kotlinx.android.synthetic.main.list_header.view.*

/**
 * A [RecyclerView.Adapter] for displaying [ListHeader]s. If necessary, will attempt to fetch images
 * from the [imageSource] and notify the [listener] of interaction events.
 */
class ListHeaderRecyclerViewAdapter(
    private val imageSource: ImageSource? = null,
    private val listener: ListHeader.InteractionListener? = null
) : RecyclerView.Adapter<ListHeaderRecyclerViewAdapter.ViewHolder>() {

    private var item: ListHeader? = null

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contents = item?.getListHeaderContents()
        if (contents == null) {
            hideViewHolder(holder)
            return
        }
        showViewHolder(holder)

        holder.imageUrl = contents.mImageUrl
        holder.titleText.text = contents.mTitle
        holder.descText.text = contents.mText

        holder.imageView.setImageDrawable(null)
        if (contents.mImageUrl != null) {
            imageSource?.getImage(contents.mImageUrl) { url, image ->
                if (url == holder.imageUrl) holder.imageView.setImageDrawable(image)
            }
        }

        val followIconId = when (contents.mFollowing) {
            true -> R.drawable.ic_star_24dp
            false -> R.drawable.ic_star_border_24dp
        }

        val followIcon = ResourcesCompat.getDrawable(holder.view.resources, followIconId, null)
        holder.following.setImageDrawable(followIcon)

        holder.following.setOnClickListener { listener?.onFollowClick() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_header, parent, false)
        return ViewHolder(view)
    }

    /** Sets what is shown in the header. */
    fun setItem(newItem: ListHeader?) {
        val itemChanged = item != newItem
        item = newItem
        if (itemChanged) notifyItemChanged(0)
    }

    private var viewPadding = 0
    private fun hideViewHolder(holder: ViewHolder) {
        // Set top-level view padding to 0 and hide children to create the illusion of no header.
        // Assume all edges of the header has the same padding, and save that value before setting
        // to 0 so that it can be restored when an actual header is desired.
        (holder.view as? ViewGroup)?.children?.forEach { it.visibility = View.GONE }
        viewPadding = holder.view.paddingTop
        holder.view.setPadding(0)
    }
    private fun showViewHolder(holder: ViewHolder) {
        (holder.view as? ViewGroup)?.children?.forEach { it.visibility = View.VISIBLE }
        holder.view.setPadding(viewPadding)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.image
        val titleText: TextView = view.title
        val descText: TextView = view.description
        val following: ImageView = view.following
        var imageUrl: String? = null
    }
}