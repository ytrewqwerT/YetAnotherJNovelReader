package com.example.yetanotherjnovelreader.common

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
import com.example.yetanotherjnovelreader.data.RemoteRepository

private const val TAG = "PartViewModel"

class PartViewModel(
    private val remoteRepository: RemoteRepository,
    private val resources: Resources
) : ViewModel() {
    private val contents = MutableLiveData<Spanned>()

    fun getContents(): LiveData<Spanned> = contents
    fun setContents(newContents: Spanned) {
        contents.value = insertImages(newContents)
    }

    private fun insertImages(spanned: Spanned): Spanned {
        val spanBuilder =
            (spanned as? SpannableStringBuilder) ?: SpannableStringBuilder(spanned)

        for (img in spanBuilder.getSpans(0, spanned.length, ImageSpan::class.java)) {
            remoteRepository.imageLoader.get(img.source, object : ImageLoader.ImageListener {
                override fun onResponse(
                    response: ImageLoader.ImageContainer?,
                    isImmediate: Boolean
                ) {
                    Log.d(TAG, "Got response for image ${img.source}")
                    if (response?.bitmap != null) {
                        Log.d(TAG, "Got image from ${img.source}")
                        val drawable = BitmapDrawable(resources, response.bitmap)
                        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                        val newImg = ImageSpan(drawable)

                        val start = spanBuilder.getSpanStart(img)
                        val end = spanBuilder.getSpanEnd(img)
                        spanBuilder.removeSpan(img)
                        spanBuilder.setSpan(newImg, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        contents.value = spanBuilder
                    }
                }

                override fun onErrorResponse(error: VolleyError?) {
                    Log.e(TAG, "Failed to get image: $error")
                }

            })
        }
        return spanBuilder
    }

    class PartViewModelFactory(
        private val remote: RemoteRepository,
        private val resources: Resources
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PartViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PartViewModel(remote, resources) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}