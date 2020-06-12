package com.ytrewqwert.yetanotherjnovelreader.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.android.volley.toolbox.ImageLoader
import com.jakewharton.disklrucache.DiskLruCache
import com.ytrewqwert.yetanotherjnovelreader.BuildConfig
import java.io.File
import java.util.*

/** An [ImageLoader.ImageCache] that saves images to local storage. */
class DiskImageLoader(appContext: Context) : ImageLoader.ImageCache {
    companion object {
        private const val TAG = "DiskImageLoader"
        private const val MAX_KEY_LEN = 64
    }

    private val cache: DiskLruCache

    init {
        val cacheDir = File(appContext.cacheDir, "disklrucache")
        cache = DiskLruCache.open(cacheDir, BuildConfig.VERSION_CODE, 1, 1 shl 24)
    }

    override fun getBitmap(url: String?): Bitmap? {
        if (url == null) return null

        val snapshot = cache.get(urlToName(url)) ?: return null
        val bytes = snapshot.getInputStream(0).readBytes()
        val result = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        Log.d(TAG, "Got $result from $url")
        return result
    }
    override fun putBitmap(url: String?, bitmap: Bitmap?) {
        if (url == null || bitmap == null) return
        cache.edit(urlToName(url))?.apply {
            val stream = newOutputStream(0)
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                commit()
            } else {
                Log.e(TAG, "Failed to encode $bitmap for $url")
                abort()
            }
        }
    }

    private fun urlToName(url: String): String {
        var result = if (url.length <= MAX_KEY_LEN) {
            url.toLowerCase(Locale.ENGLISH)
        } else {
            url.substring(url.length - MAX_KEY_LEN).toLowerCase(Locale.ENGLISH)
        }
        result = result.replace(Regex("[^a-z^0-9]"), "_")
        return result
    }
}