package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.text.Spanned
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent

/** Exposes data about what to show in each [PagedReaderPageFragment] in a [PagedReaderFragment]. */
class PagedReaderViewModel : ViewModel() {
    private val pages = ArrayList<MutableLiveData<CharSequence>>()
    val fullContent = SingleLiveEvent<Spanned>()
    val pageCount = SingleLiveEvent<Int>()

    fun setPages(newPages: List<CharSequence>) {
        for (i in newPages.indices) {
            while (i >= pages.size) pages.add(MutableLiveData())
            pages[i].value = newPages[i]
        }
        pageCount.value = newPages.size
    }

    fun getPageContent(pageNum: Int): LiveData<CharSequence> {
        while (pageNum >= pages.size) pages.add(MutableLiveData())
        return pages[pageNum]
    }
}