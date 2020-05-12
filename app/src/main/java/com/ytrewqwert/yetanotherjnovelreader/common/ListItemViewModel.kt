package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.VolumeFull
import kotlinx.coroutines.launch

class ListItemViewModel(private val repository: Repository) : ViewModel() {

    private val itemLists = ArrayList<MutableLiveData< List<ListItem>? >>()
    private val isReloading = ArrayList<MutableLiveData< Boolean >>()
    val itemClickedEvent = SingleLiveEvent<ItemClickEvent>()

    suspend fun getImage(source: String): Bitmap? = repository.getImage(source)

    fun getItemList(fragmentId: Int): LiveData< List<ListItem>? > {
        while (fragmentId >= itemLists.size) itemLists.add(MutableLiveData())
        return itemLists[fragmentId]
    }
    fun getIsReloading(fragmentId: Int): LiveData<Boolean> {
        while (fragmentId >= isReloading.size) isReloading.add(MutableLiveData(false))
        return isReloading[fragmentId]
    }

    fun setItemList(fragmentId: Int, list: List<ListItem>?) {
        while (fragmentId >= itemLists.size) itemLists.add(MutableLiveData())
        itemLists[fragmentId].value = list
    }
    fun setIsReloading(fragmentId: Int, reloading: Boolean) {
        while (fragmentId >= isReloading.size) isReloading.add(MutableLiveData())
        isReloading[fragmentId].value = reloading
    }

    fun listItemFragmentViewOnClick(fragmentId: Int, item: ListItem) {
        itemClickedEvent.value = ItemClickEvent(fragmentId, item)
    }

    fun toggleFollowItem(item: ListItem) {
        var serieId: String
        var following: Boolean
        when (item) {
            is PartFull -> {
                serieId = item.part.serieId
                following = item.following != null
            }
            is VolumeFull -> {
                serieId = item.volume.serieId
                following = item.following != null
            }
            is SerieFull -> {
                serieId = item.serie.id
                following = item.following != null
            }
            else -> return
        }
        val follow = Follow(serieId)
        viewModelScope.launch {
            if (following) repository.deleteFollows(follow)
            else repository.insertFollows(follow)
        }
    }

    data class ItemClickEvent(
        val fragmentId: Int,
        val item: ListItem
    )
}