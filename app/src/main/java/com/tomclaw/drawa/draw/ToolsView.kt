package com.tomclaw.drawa.draw

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.jakewharton.rxrelay2.PublishRelay
import com.tomclaw.drawa.R
import com.tomclaw.drawa.util.hide
import com.tomclaw.drawa.util.isVisible
import com.tomclaw.drawa.util.show
import com.tomclaw.drawa.util.toggle
import io.reactivex.Observable

interface ToolsView {

    fun showToolChooser(animate: Boolean = true)

    fun showColorChooser(animate: Boolean = true)

    fun showSizeChooser(animate: Boolean = true)

    fun hideChooser(animate: Boolean = true)

    fun tuneToolClicks(): Observable<Unit>

    fun tuneColorClicks(): Observable<Unit>

    fun tuneSizeClicks(): Observable<Unit>

    fun hideChooserClick(): Observable<Unit>

}

class ToolsViewImpl(view: View) : ToolsView {

    private val tuneTool: View = view.findViewById(R.id.tune_tool)
    private val tuneColor: View = view.findViewById(R.id.tune_color)
    private val tuneSize: View = view.findViewById(R.id.tune_size)
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

    init {
        tuneTool.setOnClickListener { tuneToolRelay.accept(Unit) }
        tuneColor.setOnClickListener { tuneColorRelay.accept(Unit) }
        tuneSize.setOnClickListener { tuneSizeRelay.accept(Unit) }
        toolsBackground.setOnClickListener { hideChooserRelay.accept(Unit) }
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

    override fun hideChooser(animate: Boolean) {
        if (toolsContainer.isVisible()) {
            hideTools(animate)
        }
    }

    override fun tuneToolClicks(): Observable<Unit> = tuneToolRelay

    override fun tuneColorClicks(): Observable<Unit> = tuneColorRelay

    override fun tuneSizeClicks(): Observable<Unit> = tuneSizeRelay

    override fun hideChooserClick(): Observable<Unit> = hideChooserRelay

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

}

private const val ANIMATION_DURATION: Long = 200
