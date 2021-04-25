package com.ytrewqwert.yetanotherjnovelreader

import android.app.Application
import android.content.Context
import androidx.annotation.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        private lateinit var context: Context

        fun getString(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()) =
            context.getString(stringRes, *formatArgs)
        fun getBool(@BoolRes boolRes: Int) =
            context.resources.getBoolean(boolRes)
        fun getInt(@IntegerRes integerRes: Int) =
            context.resources.getInteger(integerRes)
        fun getFloat(@DimenRes floatRes: Int) =
            context.resources.getFloat(floatRes)
        fun getFont(@FontRes fontRes: Int) =
            context.resources.getFont(fontRes)
    }
}