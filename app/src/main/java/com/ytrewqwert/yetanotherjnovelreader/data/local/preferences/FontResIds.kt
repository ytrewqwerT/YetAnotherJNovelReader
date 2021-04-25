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
    const val ALEGREYA = R.font.alegreya_family
    const val ALEGREYA_SANS = R.font.alegreya_sans_family
    const val CRIMSON_PRO = R.font.crimson_pro
    const val LORA = R.font.lora_family
    const val OPEN_SANS = R.font.open_sans_family
    const val CUTIVE_MONO = R.font.cutive_mono

    /** Returns the font resource id corresponding to [fontStr], or null if it doesn't exist. */
    fun getFontResourceId(fontStr: String): Int? = when (fontStr) {
        "alegreya" -> ALEGREYA
        "alegreya_sans" -> ALEGREYA_SANS
        "crimson_pro" -> CRIMSON_PRO
        "lora" -> LORA
        "open_sans" -> OPEN_SANS
        "cutive_mono" -> CUTIVE_MONO
        else -> null
    }
}