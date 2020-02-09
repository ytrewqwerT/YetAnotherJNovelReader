package com.example.yetanotherjnovelreader.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yetanotherjnovelreader.SingleLiveEvent

class ListItemViewModel : ViewModel() {

    private val itemLists = ArrayList<MutableLiveData< List<ListItem> >>()

    val itemClickedEvent = SingleLiveEvent<EventData>()

    fun setItemList(fragmentId: Int, list: List<ListItem>) {
        while (fragmentId >= itemLists.size) itemLists.add(MutableLiveData())
        itemLists[fragmentId].value = list
    }

    fun getItemList(fragmentId: Int): LiveData<List<ListItem>> {
        while (fragmentId >= itemLists.size) itemLists.add(MutableLiveData())
        return itemLists[fragmentId]
    }

    fun listItemFragmentViewOnClick(fragmentId: Int, item: ListItem) {
        itemClickedEvent.value =
            EventData(
                fragmentId,
                item
            )
    }

    data class EventData(
        val fragmentId: Int,
        val item: ListItem
    )
}