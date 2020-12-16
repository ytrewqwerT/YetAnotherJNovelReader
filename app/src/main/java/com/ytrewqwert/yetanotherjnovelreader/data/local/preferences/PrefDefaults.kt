package com.ytrewqwert.yetanotherjnovelreader.data.local.preferences

import com.ytrewqwert.yetanotherjnovelreader.App
import com.ytrewqwert.yetanotherjnovelreader.R

/** Defines default values for specific preferences. */
object PrefDefaults {
    val IS_HORIZONTAL = App.getBool(R.bool.d_horizontal_reader)
    val FONT_SIZE = App.getInt(R.integer.d_font_size)
    val MARGIN = App.getInt(R.integer.d_margin)
    val LINE_SPACING = App.getFloat(R.dimen.d_line_spacing)
    val PARA_SPACING = App.getFloat(R.dimen.d_para_spacing)
    val PARA_INDENT = App.getInt(R.integer.d_para_indent)
}