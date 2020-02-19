package com.ytrewqwert.yetanotherjnovelreader.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent

class ListItemViewModel : ViewModel() {

    private val itemLists = ArrayList<MutableLiveData< List<ListItem> >>()
    val itemClickedEvent = SingleLiveEvent<EventData>()

    fun getItemList(fragmentId: Int): LiveData<List<ListItem>> {
        while (fragmentId >= itemLists.size) itemLists.add(MutableLiveData())
        return itemLists[fragmentId]
    }

    fun listItemFragmentViewOnClick(fragmentId: Int, item: ListItem) {
        itemClickedEvent.value = EventData(fragmentId, item)
    }

    fun setItemList(fragmentId: Int, list: List<ListItem>) {
        while (fragmentId >= itemLists.size) itemLists.add(MutableLiveData())
        itemLists[fragmentId].value = list
    }

    data class EventData(
        val fragmentId: Int,
        val item: ListItem
    )
}