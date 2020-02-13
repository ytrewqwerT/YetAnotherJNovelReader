package com.example.yetanotherjnovelreader

import android.graphics.drawable.BitmapDrawable

fun BitmapDrawable.scaleToWidth(width: Int) {
    val height = width * intrinsicHeight / intrinsicWidth
    setBounds(0, 0, width, height)
}