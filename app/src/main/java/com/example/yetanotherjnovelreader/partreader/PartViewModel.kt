package com.example.yetanotherjnovelreader.partreader

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yetanotherjnovelreader.SingleLiveEvent
import com.example.yetanotherjnovelreader.data.Repository
import com.example.yetanotherjnovelreader.scaleToWidth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PartViewModel(
    private val repository: Repository,
    private val resources: Resources,
    private val partId: String,
    private val imgWidth: Int
) : ViewModel() {

    val initialPartProgress = SingleLiveEvent<Double>()
    private val _contents = MutableLiveData<Spanned>()
    val contents: LiveData<Spanned> get() = _contents

    private var uploadingProgress = false
    var currentPartProgress = 0.0
        set(value) {
            field = value
            uploadProgress()
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
        repository.getPart(partId) {
            if (it != null) {
                _contents.value = it
                insertImages(it)
            }
        }
        partProgress = repository.getPartProgress(partId)
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

    private fun insertImages(spanned: Spanned) {
        val spanBuilder =
            (spanned as? SpannableStringBuilder) ?: SpannableStringBuilder(spanned)
        tempImages = spanBuilder.getSpans(0, spanned.length, ImageSpan::class.java).size

        for (img in spanBuilder.getSpans(0, spanned.length, ImageSpan::class.java)) {
            repository.getImage(img.source) { bitmap ->
                if (bitmap != null) {
                    val drawable = BitmapDrawable(resources, bitmap)
                    drawable.scaleToWidth(imgWidth)
                    val newImg = ImageSpan(drawable)

                    val start = spanBuilder.getSpanStart(img)
                    val end = spanBuilder.getSpanEnd(img)
                    spanBuilder.removeSpan(img)
                    spanBuilder.setSpan(newImg, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    _contents.value = spanBuilder

                    tempImages--
                }
            }
        }
    }

    private fun uploadProgress() {
        // TODO (?): Reduce delay, but only upload after a significant amount of progress is made
        // Wait before sending to remote to prevent spam
        if (!uploadingProgress) {
            uploadingProgress = true
            viewModelScope.launch {
                delay(3000)
                repository.setPartProgress(partId, currentPartProgress)
                uploadingProgress = false
            }
        }
    }
}
