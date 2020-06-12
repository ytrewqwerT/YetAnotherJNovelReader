package com.ytrewqwert.yetanotherjnovelreader.common.listfooter

import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem

/**
 * A container interface for [InteractionListener] to mimic the structure of the [ListHeader] and
 * [ListItem] interfaces.
 */
interface ListFooter {
    /** Interface for handling of events relating to a [ListFooter]. */
    interface InteractionListener {
        /**
         * Called whenever a [ListFooter] is bound to a [ListFooterRecyclerViewAdapter.ViewHolder].
         */
        fun onFooterReached()
    }
}