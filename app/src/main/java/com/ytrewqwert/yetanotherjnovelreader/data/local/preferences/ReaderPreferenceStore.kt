package com.ytrewqwert.yetanotherjnovelreader.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.preference.PreferenceManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow

@Suppress("EXPERIMENTAL_API_USAGE")
class ReaderPreferenceStore private constructor(private val appContext: Context) {

    companion object {
        @Volatile
        private var INSTANCE: ReaderPreferenceStore? = null
        fun getInstance(context: Context): ReaderPreferenceStore = INSTANCE ?: synchronized(this) {
            INSTANCE ?: ReaderPreferenceStore(context.applicationContext).also { INSTANCE = it }
        }
    }

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext)

    private var paginated: Boolean = sharedPref.getBoolean(PrefKeys.IS_HORIZONTAL, PrefDefaults.IS_HORIZONTAL)
    private var fontSize = sharedPref.getInt(PrefKeys.FONT_SIZE, PrefDefaults.FONT_SIZE)
    private var fontStyle = getTypeface()
    private var readerMargins = getMargins()
    private var lineSpacing: Float = sharedPref.getFloat(PrefKeys.LINE_SPACING, PrefDefaults.LINE_SPACING)
    private var paraIndentation: Int = sharedPref.getInt(PrefKeys.PARA_INDENT, PrefDefaults.PARA_INDENT)
    private var paraSpacing: Float = sharedPref.getFloat(PrefKeys.PARA_SPACING, PrefDefaults.PARA_SPACING)
    private var pageTurnAreas = getPageTurnAreasPc()

    val readerSettings: ReaderPreferences get() = ReaderPreferences(
        paginated, fontSize, fontStyle, readerMargins,
        lineSpacing, paraIndentation, paraSpacing, pageTurnAreas
    )
    val readerSettingsFlow = channelFlow {
        offer(readerSettings)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            with (PrefKeys) {
                when (changedKey) {
                    IS_HORIZONTAL -> {
                        paginated = sharedPref.getBoolean(IS_HORIZONTAL, PrefDefaults.IS_HORIZONTAL)
                    }
                    FONT_SIZE -> {
                        fontSize = sharedPref.getInt(FONT_SIZE, PrefDefaults.FONT_SIZE)
                    }
                    FONT_STYLE -> {
                        fontStyle = getTypeface()
                    }
                    MARGIN_TOP, MARGIN_BOTTOM, MARGIN_LEFT, MARGIN_RIGHT -> {
                        readerMargins = getMargins()
                    }
                    LINE_SPACING -> {
                        lineSpacing = sharedPref.getFloat(LINE_SPACING, PrefDefaults.LINE_SPACING)
                    }
                    PARA_INDENT -> {
                        paraIndentation = sharedPref.getInt(PARA_INDENT, PrefDefaults.PARA_INDENT)
                    }
                    PARA_SPACING -> {
                        paraSpacing = sharedPref.getFloat(PARA_SPACING, PrefDefaults.PARA_SPACING)
                    }
                    PAGE_TURN_AREA_LEFT, PAGE_TURN_AREA_RIGHT -> {
                        pageTurnAreas = getPageTurnAreasPc()
                    }
                    else -> return@OnSharedPreferenceChangeListener
                }
            }
            offer(readerSettings)
        }
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPref.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private fun getTypeface(): Typeface {
        val fontStr = sharedPref.getString(PrefKeys.FONT_STYLE, "default")!!
        val fontResId = FontResIds.getFontResourceId(fontStr) ?: return Typeface.DEFAULT
        return appContext.resources.getFont(fontResId)
    }

    private fun getMargins(): Margins {
        val top = sharedPref.getInt(PrefKeys.MARGIN_TOP, PrefDefaults.MARGIN)
        val bottom = sharedPref.getInt(PrefKeys.MARGIN_BOTTOM, PrefDefaults.MARGIN)
        val left = sharedPref.getInt(PrefKeys.MARGIN_LEFT, PrefDefaults.MARGIN)
        val right = sharedPref.getInt(PrefKeys.MARGIN_RIGHT, PrefDefaults.MARGIN)
        return Margins(top, bottom, left, right)
    }

    private fun getPageTurnAreasPc(): Margins {
        val left = sharedPref.getInt(PrefKeys.PAGE_TURN_AREA_LEFT, PrefDefaults.PAGE_TURN_AREA)
        val right = sharedPref.getInt(PrefKeys.PAGE_TURN_AREA_RIGHT, PrefDefaults.PAGE_TURN_AREA)
        return Margins(0, 0, left, right)
    }

    /** A POJO defining the size of margins around each edge. */
    data class Margins(val top: Int, val bottom: Int, val left: Int, val right: Int)

    /** Aggregates reader preferences to a single object. */
    data class ReaderPreferences(
        val isHorizontal: Boolean,
        val fontSize: Int,
        val fontStyle: Typeface,
        val marginsDp: Margins,
        val lineSpacing: Float,
        val paraIndent: Int,
        val paraSpacing: Float,
        val pageTurnAreasPc: Margins
    )
}