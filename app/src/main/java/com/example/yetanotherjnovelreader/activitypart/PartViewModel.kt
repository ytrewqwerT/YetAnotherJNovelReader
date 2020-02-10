package com.example.yetanotherjnovelreader.activitypart

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.example.yetanotherjnovelreader.data.Repository

private const val TAG = "PartViewModel"

class PartViewModel(
    private val repository: Repository,
    private val resources: Resources,
    partId: String,
    private val imgWidth: Int
) : ViewModel() {

    private val _contents = MutableLiveData<Spanned>()
    val contents: LiveData<Spanned> get() = _contents

    init {
        repository.getPart(partId) {
            if (it != null) {
                _contents.value = it
                insertImages(it)
            }
        }
    }

    private fun insertImages(spanned: Spanned) {
        val spanBuilder =
            (spanned as? SpannableStringBuilder) ?: SpannableStringBuilder(spanned)

        for (img in spanBuilder.getSpans(0, spanned.length, ImageSpan::class.java)) {
            repository.imageLoader.get(img.source, object : ImageLoader.ImageListener {
                override fun onResponse(
                    response: ImageLoader.ImageContainer?,
                    isImmediate: Boolean
                ) {
                    Log.d(TAG, "Got response for image ${img.source}")
                    if (response?.bitmap != null) {
                        Log.d(TAG, "Got image from ${img.source}")
                        val drawable = BitmapDrawable(resources, response.bitmap)

                        drawable.scaleToWidth(imgWidth)
                        val newImg = ImageSpan(drawable)

                        val start = spanBuilder.getSpanStart(img)
                        val end = spanBuilder.getSpanEnd(img)
                        spanBuilder.removeSpan(img)
                        spanBuilder.setSpan(newImg, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        _contents.value = spanBuilder
                    }
                }

                override fun onErrorResponse(error: VolleyError?) {
                    Log.e(TAG, "Failed to get image: $error")
                }

            })
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