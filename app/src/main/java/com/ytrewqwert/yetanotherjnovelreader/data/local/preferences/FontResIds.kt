package com.ytrewqwert.yetanotherjnovelreader.data.local.preferences

import android.content.res.Resources
import com.ytrewqwert.yetanotherjnovelreader.R

/**
 * Contains a mapping from the name of a font-family to its corresponding resource id.
 *
 * Having to modify this every time a font is added is not ideal, but the only clean alternative
 * is [Resources.getIdentifier], which is discouraged for its inefficiency (and even if that wasn't
 * the case, [R.array.font_files] and [R.array.font_names] are still needed for setting the
 * user preference).
 */
object FontResIds {
    /** Returns the font resource id corresponding to [fontStr], or null if it doesn't exist. */
    fun getFontResourceId(fontStr: String): Int? = when (fontStr) {
        "alegreya" -> R.font.alegreya_family
        "alegreya_sans" -> R.font.alegreya_sans_family
        "crimson_pro" -> R.font.crimson_pro_family
        "lora" -> R.font.lora_family
        "open_sans" -> R.font.open_sans_family
        else -> null
    }
}