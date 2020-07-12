package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.drawable.Drawable

/**
 * Interface enabling a class to be used as a source for images.
 */
interface ImageSource {
    /**
     * Requests an image from this [ImageSource].
     *
     * @param[source] The URL where the image can be found.
     * @param[callback] A callback to provide the image back to the requester.
     */
    fun getImage(source: String, callback: (source: String, image: Drawable?) -> Unit)
}