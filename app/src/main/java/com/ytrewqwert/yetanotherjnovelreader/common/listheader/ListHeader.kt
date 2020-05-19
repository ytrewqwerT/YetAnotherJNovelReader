package com.ytrewqwert.yetanotherjnovelreader.common.listheader

interface ListHeader {
    fun getListHeaderContents(): Contents

    data class Contents(
        val mImageUrl: String? = null,
        val mTitle: String? = null,
        val mText: String? = null
    )
}