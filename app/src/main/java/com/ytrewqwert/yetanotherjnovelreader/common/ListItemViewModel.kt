package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.JobHolder
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
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

    fun getHeaderList(fragId: Int): MutableLiveData<List<ListHeader>?> = getHandler(fragId).header
    fun getItemList(fragId: Int): MutableLiveData<List<ListItem>?> = getHandler(fragId).items
    fun getIsReloading(fragId: Int): MutableLiveData<Boolean> = getHandler(fragId).reloading

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

    data class ItemClickEvent(
        val fragmentId: Int,
        val item: ListItem
    )

    data class ListItemSource(
        val sourceFlow: Flow<List<ListItem>>,
        val fetchItems: suspend (amount: Int, offset: Int) -> Boolean
    )

    fun setSource(fragId: Int, source: ListItemSource) { getHandler(fragId).setDataSource(source) }
    fun reload(fragId: Int) { getHandler(fragId).reload() }
    fun fetchNextPage(fragId: Int) { getHandler(fragId).fetchNextPage() }

    private inner class SingleListHandler {
        private var listItemSource: ListItemSource? = null
        private val listItemFlowCollector = JobHolder()
        private var listItemFetcher: Job? = null

        val header = MutableLiveData<List<ListHeader>?>(emptyList())
        val items = MutableLiveData<List<ListItem>?>(emptyList())
        val reloading = MutableLiveData(true)

        private var itemsCap = PAGE_SIZE

        fun setDataSource(newSource: ListItemSource) {
            listItemSource = newSource
            listItemFlowCollector.job = viewModelScope.launch {
                newSource.sourceFlow.collect {
                    items.value = it.subList(0, it.size.coerceAtMost(itemsCap))
                }
            }
        }

        fun reload() {
            itemsCap = 0
            listItemFetcher?.cancel()
            listItemFetcher = null
            fetchNextPage()
        }

        fun fetchNextPage() {
            if (listItemFetcher?.isCompleted == false) return
            itemsCap += PAGE_SIZE
            listItemFetcher = viewModelScope.launch {
                listItemSource?.fetchItems?.invoke(PAGE_SIZE, itemsCap - PAGE_SIZE)
            }
        }
    }
}