package com.tomclaw.drawa.util

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet

class AspectRatioImageView : AppCompatImageView {

    var aspectRatio = 1.0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = (width.toFloat() * aspectRatio).toInt()
        setMeasuredDimension(width, height)
    }

}
