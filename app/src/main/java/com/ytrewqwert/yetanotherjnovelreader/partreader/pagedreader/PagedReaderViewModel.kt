package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.text.Spanned
import android.text.TextPaint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/** Exposes data about what to show in each [PagedReaderPageFragment] in a [PagedReaderFragment]. */
class PagedReaderViewModel : ViewModel() {
    private val pages = ArrayList<MutableLiveData<CharSequence>>()

    /** The width of the page in which the part's text can be drawn, measured in px. */
    var pageWidth = 0
        set(value) { field = if (value < 0) 0 else value }
    /** The height of the page in which the part's text can be drawn, measured in px. */
    var pageHeight = 0
        set(value) { field = if (value < 0) 0 else value }
    /** The TextPaint to be used in estimating where to break new pages. */
    var paint: TextPaint? = null

    /**
     * Splits the given [content] into pages that are then stored in [pages].
     * Note that [pageWidth], [pageHeight] and [paint] must be set beforehand.
     *
     * @param[content] The content to be split.
     * @return The number of pages that the content was split into.
     */
    fun setContents(content: Spanned): Int {
        val paginator = Paginator(content, pageWidth, pageHeight, paint ?: return 0)
        val newPages = paginator.paginateText()
        for (i in newPages.indices) {
            while (i >= pages.size) pages.add(MutableLiveData())
            pages[i].value = newPages[i]
        }
        return newPages.size
    }

    fun getPageContent(pageNum: Int): LiveData<CharSequence> {
        while (pageNum >= pages.size) pages.add(MutableLiveData())
        return pages[pageNum]
    }
}