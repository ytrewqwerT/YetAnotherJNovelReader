package com.ytrewqwert.yetanotherjnovelreader.common.listheader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
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

    /**
     * Sets what is shown in the header. Note that it is intended for there to only be one header,
     * although multiple headers are possible.
     */
    fun setItems(newItems: List<ListHeader>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.image
        val titleText: TextView = view.title
        val descText: TextView = view.description
        val following: ImageView = view.following
        var imageUrl: String? = null
    }
}