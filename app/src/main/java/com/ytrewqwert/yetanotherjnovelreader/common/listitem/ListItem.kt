package com.ytrewqwert.yetanotherjnovelreader.common.listitem

/** Interface for objects that can be displayed in a [ListItemRecyclerViewAdapter] list. */
interface ListItem {
    /** Provides a representation of the object for viewing in a list. */
    fun getListItemContents(): Contents

    /** Determines whether this object should be shown as 'followed' in the list. */
    fun isFollowed(): Boolean = false

    /**
     * Contains information on what to show for a [ListItem] in the list.
     *
     * @property[mTitle] The title text.
     * @property[mText] Supplementary description text.
     * @property[mImageUrl] A URL identifying which image to show.
     * @property[progress] A value between 0 and 1 indicating how full the progress gauge should be.
     * @property[clickable] Whether or not the item is clickable. Greys the item if not clickable.
     * @property[isFollowing] Determines whether the 'follow' icon is solid or hollow (true/false).
     */
    data class Contents(
        val mTitle: String? = null,
        val mText: String? = null,
        val mImageUrl: String? = null,
        val progress: Double? = null,
        val clickable: Boolean = true,
        val isFollowing: Boolean = false
    )

    /** Interface for handling of events relating to a [ListItem]. */
    interface InteractionListener {
        /**
         * Called when a [ListItem] is clicked by the user.
         *
         * @param[item] The [ListItem] that was clicked.
         */
        fun onClick(item: ListItem)

        /**
         * Called when a [ListItem]'s 'follow' button is clicked by the user.
         *
         * @param[item] The [ListItem] that had it's 'follow' button clicked.
         */
        fun onFollowClick(item: ListItem)
    }
}