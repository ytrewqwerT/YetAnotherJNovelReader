package com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.data.FetchResult
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class SwipeableListViewModel<T : Any>(private val repository: Repository) : ViewModel() {

    private val mItems = MutableLiveData<List<T>>(emptyList())
    private val mRefreshing = MutableLiveData(false)
    private val mHasMorePages = MutableLiveData(false)

    /** Contains the items to be shown in the list */
    val items: LiveData<List<T>> = mItems
    /** Indicates whether the SwipeRefreshLayout's refreshing icon should be shown. */
    val refreshing: LiveData<Boolean> = mRefreshing
    /** Indicates whether more pages are available for loading in. */
    val hasMorePages: LiveData<Boolean> = mHasMorePages

    private var itemsCap = PAGE_SIZE
    private var itemsFetcher: Job? = null
    private var latestItems: List<T> = emptyList()

    /** A Flow for the list of items that are to be displayed. */
    abstract val itemsSourceFlow: Flow<List<T>>

    /** Call in subclasses after [itemsSourceFlow] has been initialised. (Blegh) */
    protected fun collectListData() {
        viewModelScope.launch {
            itemsSourceFlow.collect {
                latestItems = it
                // Since the fetching from remote is paginated, cap the shown list to ensure
                // that the items are properly updated with remote as the user scrolls down.
                mItems.value = it.subList(0, it.size.coerceAtMost(itemsCap))
            }
        }
        refresh()
    }

    /** Reloads/resets the data shown in the list. */
    fun refresh() {
        mRefreshing.value = true
        itemsCap = 0
        itemsFetcher?.cancel()
        itemsFetcher = null
        fetchNextPage { mRefreshing.value = false }
    }

    /** Retrieves the next page of data in the list. */
    fun fetchNextPage(onComplete: () -> Unit = {}) {
        if (itemsFetcher?.isCompleted == false) return

        itemsCap += PAGE_SIZE
        itemsFetcher = viewModelScope.launch {
            mHasMorePages.value = when(performPageFetch(PAGE_SIZE, itemsCap - PAGE_SIZE)) {
                FetchResult.FULL_PAGE -> true
                FetchResult.PART_PAGE -> false
                null -> {
                    // Fetch failed; manually update list to show next cached page from DB.
                    mItems.value = latestItems.subList(0, latestItems.size.coerceAtMost(itemsCap))
                    items.value?.size != itemsCap // No more pages when all cached items are shown.
                }
            }
            onComplete()
        }
    }

    abstract suspend fun performPageFetch(amount: Int, offset: Int): FetchResult?

    /**
     * Retrieves an image.
     *
     * @param[source] The URL where the image can be found.
     * @param[callback] A callback to provide the image back to the requester.
     */
    fun getImage(source: String, callback: (source: String, image: Drawable?) -> Unit) {
        viewModelScope.launch { callback(source, repository.getImage(source)) }
    }

    companion object {
        private const val PAGE_SIZE = 50
    }
}