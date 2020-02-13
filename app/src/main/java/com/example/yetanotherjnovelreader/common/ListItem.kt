package com.example.yetanotherjnovelreader.common

interface ListItem {
    fun getListItemContents(): ListItemContents

    data class ListItemContents(
        val mTitle: String? = null,
        val mText: String? = null,
        val mImageUrl: String? = null,
        val progress: Double? = null
    )

    interface InteractionListener {
        fun onClick(item: ListItem)
    }
}