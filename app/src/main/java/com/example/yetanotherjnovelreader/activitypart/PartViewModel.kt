package com.example.yetanotherjnovelreader.activitypart

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import androidx.lifecycle.*
import com.example.yetanotherjnovelreader.SingleLiveEvent
import com.example.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "PartViewModel"

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
        repository.getPartProgress(partId) { partProgress = it ?: 0.0 }
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

    class PartViewModelFactory(
        private val repository: Repository,
        private val resources: Resources,
        private val partId: String,
        private val imgWidth: Int
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PartViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PartViewModel(repository, resources, partId, imgWidth) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

fun BitmapDrawable.scaleToWidth(width: Int) {
    Log.d(TAG, "Scaling image to width $width")
    val height = width * intrinsicHeight / intrinsicWidth
    setBounds(0, 0, width, height)
}