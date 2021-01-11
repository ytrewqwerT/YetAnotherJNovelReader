package com.ytrewqwert.yetanotherjnovelreader

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.core.content.edit
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

/**
 * Scales this Drawable's size to fit in a rectangle with the given [width] and [height],
 * maintaining the image aspect ratio.
 */
fun Drawable.scaleToSize(width: Int, height: Int) {
    val constrainedWidth: Int
    val constrainedHeight: Int
    if (intrinsicHeight * width / intrinsicWidth > height) { // Constrained by height
        constrainedWidth = intrinsicWidth * height / intrinsicHeight
        constrainedHeight = height
    } else { // Constrained by width
        constrainedWidth = width
        constrainedHeight = width * intrinsicHeight / intrinsicWidth
    }
    setBounds(0, 0, constrainedWidth, constrainedHeight)
}

/** Shortcut for setting the value of a boolean preference. */
fun SharedPreferences.setBoolean(key: String, value: Boolean) {
    edit(true) { putBoolean(key, value) }
}
/** Shortcut for setting the value of a string preference. */
fun SharedPreferences.setString(key: String, value: String?) {
    edit(true) { putString(key, value) }
}

/** Calls the provided listener each time a new page is selected. */
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

/** Calls the provided listener each time a new page is selected. */
fun ViewPager2.addOnPageSelectedListener(listener: (position: Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            listener(position)
        }
    })
}