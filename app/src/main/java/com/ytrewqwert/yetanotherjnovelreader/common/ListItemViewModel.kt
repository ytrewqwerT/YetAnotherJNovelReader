package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.Bitmap
import android.util.Log
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
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListItemViewModel(private val repository: Repository) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 20
    }

    private val lists = ArrayList<SingleListHandler>()
    val itemClickedEvent = SingleLiveEvent<ItemClickEvent>()

    fun getImage(source: String, callback: (String, Bitmap?) -> Unit) {
        viewModelScope.launch { callback(source, repository.getImage(source)) }
    }

    fun getHeaderList(fragId: Int): LiveData<List<ListHeader>?> = getHandler(fragId).header
    fun getItemList(fragId: Int): LiveData<List<ListItem>?> = getHandler(fragId).items
    fun getIsReloading(fragId: Int): LiveData<Boolean> = getHandler(fragId).reloading

    fun listItemFragmentViewOnClick(fragmentId: Int, item: ListItem) {
        itemClickedEvent.value = ItemClickEvent(fragmentId, item)
    }

    fun toggleFollowItem(item: ListItem) {
        val serieId: String
        val following: Boolean
        // Ugh
        when (item) {
            is PartFull -> {
                serieId = item.part.serieId
                following = item.isFollowed()
            }
            is VolumeFull -> {
                serieId = item.volume.serieId
                following = item.isFollowed()
            }
            is SerieFull -> {
                serieId = item.serie.id
                following = item.isFollowed()
            }
            else -> return
        }
        val follow = Follow(serieId)
        viewModelScope.launch {
            if (following) repository.deleteFollows(follow)
            else repository.insertFollows(follow)
        }
    }

    private fun padListsToSize(size: Int) {
        while (lists.size < size) lists.add(SingleListHandler())
    }

    private fun getHandler(fragId: Int): SingleListHandler {
        padListsToSize(fragId + 1)
        return lists[fragId]
    }


    fun setHeader(fragId: Int, value: ListHeader) { getHandler(fragId).setHeader(value) }
    fun setSource(fragId: Int, source: ListItemSource) { getHandler(fragId).setDataSource(source) }
    fun reload(fragId: Int) { getHandler(fragId).reload() }
    fun fetchNextPage(fragId: Int, onComplete: (morePages: Boolean) -> Unit = {}) {
        getHandler(fragId).fetchNextPage(onComplete)
    }

    data class ItemClickEvent(
        val fragmentId: Int,
        val item: ListItem
    )

    data class ListItemSource(
        val sourceFlow: Flow<List<ListItem>>,
        val fetchItems: suspend (scope: CoroutineScope, amount: Int, offset: Int) -> FetchResult?
    )

    private inner class SingleListHandler {
        private var listItemSource: ListItemSource? = null
        private val listItemFlowCollector = JobHolder()
        private var listItemFetcher: Job? = null
        private var latestItems: List<ListItem> = emptyList()

        val header = MutableLiveData<List<ListHeader>?>(emptyList())
        val items = MutableLiveData<List<ListItem>?>(emptyList())
        val reloading = MutableLiveData(false)

        private var itemsCap = PAGE_SIZE
            set(value) {
                field = value
                Log.d("SingleListHandler", "Length set to $value")
            }

        fun setHeader(value: ListHeader) { header.value = listOf(value) }
        fun setDataSource(newSource: ListItemSource) {
            listItemSource = newSource
            listItemFlowCollector.job = viewModelScope.launch {
                newSource.sourceFlow.collect {
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

        fun fetchNextPage(onComplete: (morePages: Boolean) -> Unit = {}) {
            if (listItemFetcher?.isCompleted == false) return

            itemsCap += PAGE_SIZE
            listItemFetcher = viewModelScope.launch {
                if (listItemSource == null) return@launch

                val result = listItemSource?.fetchItems?.invoke(
                    this, PAGE_SIZE, itemsCap - PAGE_SIZE
                )
                // Note that when the fetch succeeds, the result is automatically propagated to the
                // flow via a DB upsertion.
                when (result) {
                    FetchResult.FULL_PAGE -> onComplete(true)
                    FetchResult.PART_PAGE -> onComplete(false)
                    null -> {
                        // Fetch failed; show next cached page from DB.
                        items.value = latestItems.subList(
                            0, latestItems.size.coerceAtMost(itemsCap)
                        )
                    }
                }
            }
        }
    }
}