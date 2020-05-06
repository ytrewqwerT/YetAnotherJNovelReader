package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.Bitmap

interface ImageSource {
    fun getImage(source: String, callback: (url: String, image: Bitmap?) -> Unit)
}