package com.example.yetanotherjnovelreader.activitymain

import androidx.lifecycle.ViewModel
import com.example.yetanotherjnovelreader.SingleLiveEvent
import com.example.yetanotherjnovelreader.common.ListItem

class ListItemViewModel : ViewModel() {

    val itemClickedEvent = SingleLiveEvent<EventData>()

    fun listItemFragmentViewOnClick(fragmentId: Int, item: ListItem) {
        itemClickedEvent.value = EventData(fragmentId, item)
    }

    data class EventData(
        val fragmentId: Int,
        val item: ListItem
    )
}