package com.ytrewqwert.yetanotherjnovelreader.common

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.ytrewqwert.yetanotherjnovelreader.R

class FadeInImageView : AppCompatImageView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val fadeInAnimator = AnimatorInflater.loadAnimator(context, R.animator.fade_in).apply {
        setTarget(this@FadeInImageView)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        fadeInAnimator.cancel()
        if (drawable != null) fadeInAnimator.start()
    }
}