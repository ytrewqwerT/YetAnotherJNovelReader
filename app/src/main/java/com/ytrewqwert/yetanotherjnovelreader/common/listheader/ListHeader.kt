package com.ytrewqwert.yetanotherjnovelreader.common.listheader


/** Interface for objects that can be displayed in a [ListHeaderRecyclerViewAdapter] list. */
interface ListHeader {
    /** Provides a representation of the object for viewing as the header in a list. */
    fun getListHeaderContents(): Contents

    /**
     * Contains information on what to show for a [ListHeader] in the list.
     *
     * @property[mImageUrl] A URL identifying which image to show.
     * @property[mTitle] The title text.
     * @property[mText] Supplementary description text.
     * @property[mFollowing] Whether the follow item is followed.
     */
    data class Contents(
        val mImageUrl: String? = null,
        val mTitle: String? = null,
        val mText: String? = null,
        val mFollowing: Boolean = false
    )

    /** Interface for handling of events relating to a [ListHeader]. */
    interface InteractionListener {
        /** Called when a [ListHeader]'s 'follow' button is clicked by the user. */
        fun onFollowClick()
    }
}