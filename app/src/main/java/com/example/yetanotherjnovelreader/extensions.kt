package com.example.yetanotherjnovelreader

import android.graphics.drawable.BitmapDrawable
import androidx.viewpager.widget.ViewPager

fun BitmapDrawable.scaleToWidth(width: Int) {
    val height = width * intrinsicHeight / intrinsicWidth
    setBounds(0, 0, width, height)
}

fun ViewPager.addOnPageSelectedListener(listener: (position: Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(
            position: Int, positionOffset: Float, positionOffsetPixels: Int
        ) {}

        override fun onPageSelected(position: Int) {
            listener(position)
        }
    })
}