package com.ytrewqwert.yetanotherjnovelreader

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.core.content.edit
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

/** Set's this BitmapDrawable's width to equal the given [width], retaining image aspect ratio. */
fun Drawable.scaleToWidth(width: Int) {
    val height = width * intrinsicHeight / intrinsicWidth
    setBounds(0, 0, width, height)
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