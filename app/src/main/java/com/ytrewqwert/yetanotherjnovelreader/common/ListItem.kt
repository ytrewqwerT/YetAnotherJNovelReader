package com.ytrewqwert.yetanotherjnovelreader.common

interface ListItem {
    fun getListItemContents(): ListItemContents

    data class ListItemContents(
        val mTitle: String? = null,
        val mText: String? = null,
        val mImageUrl: String? = null,
        val progress: Double? = null,
        val clickable: Boolean = true,
        val isFollowing: Boolean = false
    )

    interface InteractionListener {
        fun onClick(item: ListItem)
        fun onFollowClick(item: ListItem)
    }
}