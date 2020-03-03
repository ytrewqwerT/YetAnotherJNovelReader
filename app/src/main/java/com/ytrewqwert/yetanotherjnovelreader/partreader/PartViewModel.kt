package com.ytrewqwert.yetanotherjnovelreader.partreader

import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.scaleToWidth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PartViewModel(
    private val repository: Repository,
    private val resources: Resources,
    private val partId: String,
    private val imgWidth: Int
) : ViewModel() {

    val errorEvent = SingleLiveEvent<String>()

    val initialPartProgress = SingleLiveEvent<Double>()
    private val _contents = MutableLiveData<Spanned>()
    val contents: LiveData<Spanned> get() = _contents

    val fontSize: Int get() = repository.fontSize
    val fontStyle: Typeface get() = repository.fontStyle
    val margin: Int get() = repository.readerMargin

    private var progressChanged = false
    var currentPartProgress = 0.0
        set(value) {
            field = value
            progressChanged = true
        }

    private var tempImages = -1
        set(value) {
            field = value
            if (value == 0 && partProgress != -1.0) setInitialPartsProgress(partProgress)
        }
    private var partProgress = -1.0
        set(value) {
            field = value
            if (tempImages == 0) setInitialPartsProgress(value)
        }

    init {
        viewModelScope.launch {
            val partData = repository.getPart(partId)
            if (partData != null) {
                _contents.value = partData
                insertImages(partData)
            } else {
                errorEvent.value = "Failed to get part data"
            }
        }
        partProgress = repository.getPartProgress(partId)
    }

    private suspend fun insertImages(spanned: Spanned) {
        val spanBuilder =
            (spanned as? SpannableStringBuilder) ?: SpannableStringBuilder(spanned)

        coroutineScope {
            for (img in spanBuilder.getSpans(0, spanned.length, ImageSpan::class.java)) {
                launch { insertImage(img, spanBuilder) }
            }
        }
        tempImages = 0
    }

    private suspend fun insertImage(
        img: ImageSpan,
        spanBuilder: SpannableStringBuilder
    ) {
        val bitmap = repository.getImage(img.source) ?: return
        val drawable = BitmapDrawable(resources, bitmap)
        drawable.scaleToWidth(imgWidth)
        val newImg = ImageSpan(drawable)

        val start = spanBuilder.getSpanStart(img)
        val end = spanBuilder.getSpanEnd(img)
        spanBuilder.removeSpan(img)
        spanBuilder.setSpan(newImg, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        _contents.value = spanBuilder
    }

    private fun setInitialPartsProgress(value: Double) {
        viewModelScope.launch {
            // Responding too quickly upon activity creation results in
            // the scrollview's position not being updated, so wait.
            delay(100)
            initialPartProgress.value = value
            currentPartProgress = value
        }
    }

    fun uploadProgressNow() {
        // Only upload if the value has changed since last upload
        if (progressChanged) {
            viewModelScope.launch { repository.setPartProgress(partId, currentPartProgress) }
            progressChanged = false
        }
    }
}
