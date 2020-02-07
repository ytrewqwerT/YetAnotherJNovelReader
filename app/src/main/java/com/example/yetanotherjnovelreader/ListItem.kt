package com.example.yetanotherjnovelreader

interface ListItem {
    fun getListItemContents(): ListItemContents

    data class ListItemContents(
        val mTitle: String?,
        val mText: String?,
        val mImageUrl: String
    )
}