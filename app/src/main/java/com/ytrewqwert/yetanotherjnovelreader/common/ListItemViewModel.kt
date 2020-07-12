package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.JobHolder
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.FetchResult
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** Exposes data for one or more [ListItemFragment]s. */
class ListItemViewModel(private val repository: Repository) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 50
    }

    /**
     * Contains data about the clicking of a [ListItem].
     *
     * @property[fragId] The ID of the list where the click occurred.
     * @property[item] The ListItem that was clicked.
     */
    data class ItemClickEvent(
        val fragId: Int,
        val item: ListItem
    )

    /**
     * Identifies where a list should source data from.
     *
     * @property[sourceFlow] A flow for the items to be shown in the list.
     * @property[fetchItems] A callback for fetching more items in the list.
     */
    data class ListItemSource(
        val sourceFlow: Flow<List<ListItem>>,
        val fetchItems: suspend (amount: Int, offset: Int, followedOnly: Boolean) -> FetchResult?
    )

    private val lists = ArrayList<SingleListHandler>()

    /** Triggers when an item in a list is clicked. */
    val itemClickedEvent = SingleLiveEvent<ItemClickEvent>()

    private var isFilterFollowing = false

    init {
        viewModelScope.launch {
            repository.isFilterFollowing.collect {
                isFilterFollowing = it
                for (handler in lists) handler.reload()
            }
        }
    }

    /**
     * Retrieves an image.
     *
     * @param[source] The URL where the image can be found.
     * @param[callback] A callback to provide the image back to the requester.
     */
    fun getImage(source: String, callback: (source: String, image: Drawable?) -> Unit) {
        viewModelScope.launch { callback(source, repository.getImage(source)) }
    }

    /** Retrieves the header list LiveData corresponding to the list with ID [fragId]. */
    fun getHeaderList(fragId: Int): LiveData<List<ListHeader>?> = getHandler(fragId).header
    /** Retrieves the item list LiveData corresponding to the list with ID [fragId]. */
    fun getItemList(fragId: Int): LiveData<List<ListItem>?> = getHandler(fragId).items
    /** Retrieves a LiveData indicating reloading state for the [fragId]'s list. */
    fun getIsReloading(fragId: Int): LiveData<Boolean> = getHandler(fragId).reloading
    /** Retrieves a LiveData indicating whether more pages are available for the [fragId]'s list. */
    fun getHasMorePages(fragId: Int): LiveData<Boolean> = getHandler(fragId).morePages

    /** Sets the header for the list with ID [fragId]. */
    fun setHeader(fragId: Int, value: ListHeader) { getHandler(fragId).setHeader(value) }
    /** Sets the source from which more items in the [fragId]'s list can be retrieved. */
    fun setSource(fragId: Int, source: ListItemSource) { getHandler(fragId).setDataSource(source) }
    /** Refreshes the [fragId]'s list and resets it to showing only the first page of content. */
    fun reload(fragId: Int) { getHandler(fragId).reload() }
    /** Fetches the next set of items to be shown in [fragId]'s list and appends it to that list. */
    fun fetchNextPage(fragId: Int) { getHandler(fragId).fetchNextPage() }

    /** Notifies a listener of [itemClickedEvent] of an [item] being clicked. */
    fun listItemFragmentViewOnClick(fragId: Int, item: ListItem) {
        itemClickedEvent.value = ItemClickEvent(fragId, item)
    }

    /** Toggle's the given [item]'s 'follow' state on/off. */
    fun toggleFollowItem(item: ListItem) {
        val following: Boolean = item.isFollowed()
        val serieId: String = when (item) {
            is PartFull -> item.part.serieId
            is VolumeFull -> item.volume.serieId
            is SerieFull -> item.serie.id
            else -> return
        }

        viewModelScope.launch {
            if (following) repository.unfollowSeries(serieId)
            else repository.followSeries(serieId)
        }
    }

    private fun padListsToSize(size: Int) {
        while (lists.size < size) lists.add(SingleListHandler())
    }

    private fun getHandler(fragId: Int): SingleListHandler {
        padListsToSize(fragId + 1)
        return lists[fragId]
    }

    private inner class SingleListHandler {
        private var listItemSource: ListItemSource? = null
        private val listItemFlowCollector = JobHolder()
        private var listItemFetcher: Job? = null
        private var latestItems: List<ListItem> = emptyList()

        val header = MutableLiveData<List<ListHeader>?>(emptyList())
        val items = MutableLiveData<List<ListItem>?>(emptyList())
        val reloading = MutableLiveData(false)
        val morePages = MutableLiveData(false)

        private var itemsCap = PAGE_SIZE

        fun setHeader(value: ListHeader) { header.value = listOf(value) }
        fun setDataSource(newSource: ListItemSource) {
            listItemSource = newSource
            listItemFlowCollector.job = viewModelScope.launch {
                @Suppress("EXPERIMENTAL_API_USAGE")
                newSource.sourceFlow
                    .combine(repository.isFilterFollowing) { items, filterOn ->
                        items.filter { !filterOn || it.isFollowed() }
                    }.collect {
                        latestItems = it
                        // Since the fetching from remote is paginated, cap the shown list to ensure
                        // that the items are properly updated with remote as the user scrolls down.
                        items.value = it.subList(0, it.size.coerceAtMost(itemsCap))
                    }
            }
            reload()
        }

        fun reload() {
            reloading.value = true
            itemsCap = 0
            listItemFetcher?.cancel()
            listItemFetcher = null
            fetchNextPage { reloading.value = false }
        }

        fun fetchNextPage(onComplete: () -> Unit = {}) {
            if (listItemFetcher?.isCompleted == false) return

            itemsCap += PAGE_SIZE
            listItemFetcher = viewModelScope.launch {
                val result = performFetch()
                morePages.value = result
                onComplete()
            }
        }

        // Returns true if there may still be more pages that can be shown. False otherwise.
        private suspend fun performFetch(): Boolean {
            if (listItemSource == null) return false

            val result = listItemSource?.fetchItems?.invoke(
                PAGE_SIZE, itemsCap - PAGE_SIZE, isFilterFollowing
            )
            // Note that when the fetch succeeds, the result is automatically propagated to the
            // flow via a DB upsertion, so there's no need to manually update the items list.
            return when (result) {
                FetchResult.FULL_PAGE -> true
                FetchResult.PART_PAGE -> false
                null -> {
                    // Fetch failed; manually update list to show next cached page from DB.
                    items.value = latestItems.subList(0, latestItems.size.coerceAtMost(itemsCap))
                    items.value?.size != itemsCap
                }
            }
        }
    }
}