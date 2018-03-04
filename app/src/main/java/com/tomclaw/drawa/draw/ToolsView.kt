package com.tomclaw.drawa.draw

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.draw.tools.SIZE_L
import com.tomclaw.drawa.draw.tools.SIZE_M
import com.tomclaw.drawa.draw.tools.SIZE_S
import com.tomclaw.drawa.draw.tools.SIZE_XL
import com.tomclaw.drawa.draw.tools.SIZE_XXL
import com.tomclaw.drawa.draw.tools.TYPE_BRUSH
import com.tomclaw.drawa.draw.tools.TYPE_ERASER
import com.tomclaw.drawa.draw.tools.TYPE_FILL
import com.tomclaw.drawa.draw.tools.TYPE_FLUFFY
import com.tomclaw.drawa.draw.tools.TYPE_MARKER
import com.tomclaw.drawa.draw.tools.TYPE_PENCIL
import com.tomclaw.drawa.draw.view.PaletteView
import com.tomclaw.drawa.util.hide
import com.tomclaw.drawa.util.isVisible
import com.tomclaw.drawa.util.show
import com.tomclaw.drawa.util.toggle
import io.reactivex.Observable

interface ToolsView {

    fun showToolChooser(animate: Boolean = true)

    fun showColorChooser(animate: Boolean = true)

    fun showSizeChooser(animate: Boolean = true)

    fun setToolSelected(toolType: Int)

    fun setColorSelected(color: Int)

    fun setSizeSelected(size: Int)

    fun hideChooser(animate: Boolean = true)

    fun tuneToolClicks(): Observable<Unit>

    fun tuneColorClicks(): Observable<Unit>

    fun tuneSizeClicks(): Observable<Unit>

    fun hideChooserClick(): Observable<Unit>

    fun toolSelected(): Observable<Int>

    fun colorSelected(): Observable<Int>

    fun sizeSelected(): Observable<Int>

}

class ToolsViewImpl(view: View) : ToolsView {

    private val tuneTool: ImageView = view.findViewById(R.id.tune_tool)
    private val tuneColor: ImageView = view.findViewById(R.id.tune_color)
    private val tuneSize: ImageView = view.findViewById(R.id.tune_size)
    private val toolsBackground: View = view.findViewById(R.id.tools_background)
    private val toolsContainer: View = view.findViewById(R.id.tools_container)
    private val toolsWrapper: View = view.findViewById(R.id.tools_wrapper)
    private val toolChooser: View = view.findViewById(R.id.tool_chooser)
    private val colorChooser: View = view.findViewById(R.id.color_chooser)
    private val sizeChooser: View = view.findViewById(R.id.size_chooser)

    private val tuneToolRelay = PublishRelay.create<Unit>()
    private val tuneColorRelay = PublishRelay.create<Unit>()
    private val tuneSizeRelay = PublishRelay.create<Unit>()
    private val hideChooserRelay = PublishRelay.create<Unit>()
    private val toolRelay = PublishRelay.create<Int>()
    private val colorRelay = PublishRelay.create<Int>()
    private val sizeRelay = PublishRelay.create<Int>()

    init {
        tuneTool.setOnClickListener { tuneToolRelay.accept(Unit) }
        tuneColor.setOnClickListener { tuneColorRelay.accept(Unit) }
        tuneSize.setOnClickListener { tuneSizeRelay.accept(Unit) }
        toolsBackground.setOnClickListener { hideChooserRelay.accept(Unit) }

        view.setOnClickListener(R.id.tool_pencil, { toolRelay.accept(TYPE_PENCIL) })
        view.setOnClickListener(R.id.tool_brush, { toolRelay.accept(TYPE_BRUSH) })
        view.setOnClickListener(R.id.tool_marker, { toolRelay.accept(TYPE_MARKER) })
        view.setOnClickListener(R.id.tool_fluffy, { toolRelay.accept(TYPE_FLUFFY) })
        view.setOnClickListener(R.id.tool_fill, { toolRelay.accept(TYPE_FILL) })
        view.setOnClickListener(R.id.tool_eraser, { toolRelay.accept(TYPE_ERASER) })
        view.findViewById<PaletteView>(R.id.palette_view).colorClickListener =
                object : PaletteView.OnColorClickListener {
                    override fun onColorClicked(color: Int) {
                        colorRelay.accept(color)
                    }
                }
        view.setOnClickListener(R.id.size_s, { sizeRelay.accept(SIZE_S) })
        view.setOnClickListener(R.id.size_m, { sizeRelay.accept(SIZE_M) })
        view.setOnClickListener(R.id.size_l, { sizeRelay.accept(SIZE_L) })
        view.setOnClickListener(R.id.size_xl, { sizeRelay.accept(SIZE_XL) })
        view.setOnClickListener(R.id.size_xxl, { sizeRelay.accept(SIZE_XXL) })
    }

    override fun showToolChooser(animate: Boolean) {
        if (toolsContainer.isVisible()) {
            hideTools(animate)
        } else {
            toolChooser.show()
            colorChooser.hide()
            sizeChooser.hide()
            showTools(animate)
        }
    }

    override fun showColorChooser(animate: Boolean) {
        if (toolsContainer.isVisible()) {
            hideTools(animate)
        } else {
            toolsContainer.toggle()
            toolChooser.hide()
            colorChooser.show()
            sizeChooser.hide()
            showTools(animate)
        }
    }

    override fun showSizeChooser(animate: Boolean) {
        if (toolsContainer.isVisible()) {
            hideTools(animate)
        } else {
            toolsContainer.toggle()
            toolChooser.hide()
            colorChooser.hide()
            sizeChooser.show()
            showTools(animate)
        }
    }

    override fun setToolSelected(toolType: Int) {
        val toolIcon = when (toolType) {
            TYPE_PENCIL -> R.drawable.lead_pencil
            TYPE_BRUSH -> R.drawable.brush
            TYPE_MARKER -> R.drawable.marker
            TYPE_FLUFFY -> R.drawable.spray
            TYPE_FILL -> R.drawable.format_color_fill
            TYPE_ERASER -> R.drawable.eraser
            else -> return
        }
        tuneTool.setImageResource(toolIcon)
    }

    override fun setColorSelected(color: Int) {
        tuneColor.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    override fun setSizeSelected(size: Int) {
        val sizeIcon = when (size) {
            SIZE_S -> R.drawable.size_s
            SIZE_M -> R.drawable.size_m
            SIZE_L -> R.drawable.size_l
            SIZE_XL -> R.drawable.size_xl
            SIZE_XXL -> R.drawable.size_xxl
            else -> return
        }
        tuneSize.setImageResource(sizeIcon)
    }

    override fun hideChooser(animate: Boolean) {
        if (toolsContainer.isVisible()) {
            hideTools(animate)
        }
    }

    override fun tuneToolClicks(): Observable<Unit> = tuneToolRelay

    override fun tuneColorClicks(): Observable<Unit> = tuneColorRelay

    override fun tuneSizeClicks(): Observable<Unit> = tuneSizeRelay

    override fun hideChooserClick(): Observable<Unit> = hideChooserRelay

    override fun toolSelected(): Observable<Int> = toolRelay

    override fun colorSelected(): Observable<Int> = colorRelay

    override fun sizeSelected(): Observable<Int> = sizeRelay

    private fun showTools(animate: Boolean) {
        if (animate) {
            toolsContainer.show()
            toolsBackground.showWithAlphaAnimation()
            toolsWrapper.showWithTranslationAnimation()
        } else {
            toolsContainer.show()
            toolsBackground.show()
            toolsWrapper.show()
        }
    }

    private fun hideTools(animate: Boolean) {
        if (animate) {
            toolsBackground.hideWithAlphaAnimation({ toolsContainer.hide() })
            toolsWrapper.hideWithTranslationAnimation({})
        } else {
            toolsContainer.hide()
            toolsBackground.hide()
            toolsWrapper.hide()
        }
    }

    private fun View.showWithAlphaAnimation() {
        alpha = 0.0f
        show()
        animate()
                .setDuration(ANIMATION_DURATION)
                .alpha(1.0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(null)
    }

    private fun View.showWithTranslationAnimation() {
        show()
        translationY = height.toFloat()
        alpha = 0.0f
        animate()
                .setDuration(ANIMATION_DURATION)
                .alpha(1.0f)
                .translationY(0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(null)
    }

    private fun View.hideWithAlphaAnimation(endCallback: () -> (Unit)) {
        alpha = 1.0f
        animate()
                .setDuration(ANIMATION_DURATION)
                .alpha(0.0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        hide()
                        endCallback.invoke()
                    }
                })
    }

    private fun View.hideWithTranslationAnimation(endCallback: () -> (Unit)) {
        alpha = 1.0f
        translationY = 0f
        animate()
                .setDuration(ANIMATION_DURATION)
                .alpha(0.0f)
                .translationY(height.toFloat())
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        hide()
                        endCallback.invoke()
                    }
                })
    }

    private fun View.setOnClickListener(id: Int, listener: () -> Unit) {
        findViewById<View>(id).setOnClickListener { listener.invoke() }
    }

}

private const val ANIMATION_DURATION: Long = 200
