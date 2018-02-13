package com.tomclaw.drawa.draw

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class PaletteImageView : AppCompatImageView {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

}
