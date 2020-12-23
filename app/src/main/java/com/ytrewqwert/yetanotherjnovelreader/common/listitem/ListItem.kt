package com.ytrewqwert.yetanotherjnovelreader.common.listitem

/** Interface for objects that can be displayed in a list. */
interface ListItem {
    /** Determines whether this object should be shown as 'followed' in the list. */
    fun isFollowed(): Boolean = false
}