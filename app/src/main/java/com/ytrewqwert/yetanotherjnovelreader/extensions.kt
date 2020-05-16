package com.ytrewqwert.yetanotherjnovelreader

import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.edit
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import org.json.JSONArray
import org.json.JSONObject

fun BitmapDrawable.scaleToWidth(width: Int) {
    val height = width * intrinsicHeight / intrinsicWidth
    setBounds(0, 0, width, height)
}

inline fun <reified T> JSONArray.forEach(block: (T) -> Unit) {
    for (i in 0 until length()) {
        val item = get(i)
        if (item is T) { block(item) }
    }
}

fun SharedPreferences.setBoolean(key: String, value: Boolean) {
    edit(true) { putBoolean(key, value) }
}
fun SharedPreferences.setString(key: String, value: String?) {
    edit(true) { putString(key, value) }
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

fun ViewPager2.addOnPageSelectedListener(listener: (position: Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            listener(position)
        }
    })
}