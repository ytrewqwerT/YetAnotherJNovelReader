package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import kotlinx.coroutines.launch

class ListItemViewModel(private val repository: Repository) : ViewModel() {

    data class ContentLiveData(
        val header: MutableLiveData<List<ListHeader>?> = MutableLiveData(emptyList()),
        val item: MutableLiveData<List<ListItem>?> = MutableLiveData(emptyList()),
        val reloading: MutableLiveData<Boolean> = MutableLiveData(true)
    )
    private val lists = ArrayList<ContentLiveData>()

    val itemClickedEvent = SingleLiveEvent<ItemClickEvent>()

    fun getImage(source: String, callback: (String, Bitmap?) -> Unit) {
        viewModelScope.launch { callback(source, repository.getImage(source)) }
    }

    fun getContentLiveData(fragId: Int): ContentLiveData {
        padListsToSize(fragId + 1)
        return lists[fragId]
    }
    fun getItemList(fragId: Int): MutableLiveData<List<ListItem>?> = getContentLiveData(fragId).item
    fun getIsReloading(fragId: Int): MutableLiveData<Boolean> = getContentLiveData(fragId).reloading

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
        while (lists.size < size) lists.add(ContentLiveData())
    }

    data class ItemClickEvent(
        val fragmentId: Int,
        val item: ListItem
    )
}