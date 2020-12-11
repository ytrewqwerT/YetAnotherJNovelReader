package com.ytrewqwert.yetanotherjnovelreader.data.local.preferences

import com.ytrewqwert.yetanotherjnovelreader.App
import com.ytrewqwert.yetanotherjnovelreader.R

/** Defines keys for user preferences. */
object PrefKeys {
    /** Hidden preferences */
    const val EMAIL = "EMAIL"
    const val PASSWORD = "PASSWORD"

    const val USER_ID = "USER_ID"
    const val AUTH_TOKEN = "AUTHENTICATION_TOKEN"
    const val AUTH_DATE = "AUTHENTICATION_DATE"
    const val USERNAME = "USERNAME"
    const val IS_MEMBER = "IS_MEMBER"

    const val IS_FOLLOW = "IS_FOLLOW"

    /** User preferences */

    val IS_HORIZONTAL = App.getString(R.string.i_horizontal_reader)
    val FONT_STYLE = App.getString(R.string.i_font_style)
    val FONT_SIZE = App.getString(R.string.i_font_size)

    val MARGIN_TOP = App.getString(R.string.i_margin_top)
    val MARGIN_BOTTOM = App.getString(R.string.i_margin_bottom)
    val MARGIN_LEFT = App.getString(R.string.i_margin_left)
    val MARGIN_RIGHT = App.getString(R.string.i_margin_right)

    val LINE_SPACING = App.getString(R.string.i_line_spacing)
    val PARA_SPACING = App.getString(R.string.i_para_spacing)
    val PARA_INDENT = App.getString(R.string.i_para_indent)
}