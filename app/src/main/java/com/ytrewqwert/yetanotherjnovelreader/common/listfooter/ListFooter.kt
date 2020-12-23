package com.ytrewqwert.yetanotherjnovelreader.common.listfooter

import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader

/**
 * A container interface for [InteractionListener] to mimic the structure of the [ListHeader]
 * interface.
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