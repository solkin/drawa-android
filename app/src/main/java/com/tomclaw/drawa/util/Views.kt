package com.tomclaw.drawa.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator

fun View?.toggle() {
    if (this?.visibility == VISIBLE) hide() else show()
}

fun View?.isVisible(): Boolean = this?.visibility == VISIBLE

fun View?.show() {
    this?.visibility = VISIBLE
}

fun View?.hide() {
    this?.visibility = GONE
}

fun View.showWithAlphaAnimation(duration: Long = ANIMATION_DURATION,
                                animateFully: Boolean = true,
                                endCallback: (() -> Unit)? = null) {
    if (animateFully) {
        alpha = 0.0f
    }
    show()
    animate()
            .setDuration(duration)
            .alpha(1.0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    alpha = 1.0f
                    show()
                    endCallback?.invoke()
                }
            })
}

fun View.hideWithAlphaAnimation(duration: Long = ANIMATION_DURATION,
                                animateFully: Boolean = true,
                                endCallback: (() -> Unit)? = null) {
    if (animateFully) {
        alpha = 1.0f
    }
    animate()
            .setDuration(duration)
            .alpha(0.0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    hide()
                    alpha = 1.0f
                    endCallback?.invoke()
                }
            })
}

fun View.showWithTranslationAnimation(height: Float) {
    translationY = height
    alpha = 0.0f
    show()
    animate()
            .setDuration(ANIMATION_DURATION)
            .alpha(1.0f)
            .translationY(0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    translationY = 0f
                    alpha = 1.0f
                    show()
                }
            })
}

fun View.moveWithTranslationAnimation(fromTranslationY: Float,
                                      tillTranslationY: Float,
                                      endCallback: () -> (Unit)) {
    translationY = fromTranslationY
    animate()
            .setDuration(ANIMATION_DURATION)
            .translationY(tillTranslationY)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    translationY = 0f
                    endCallback.invoke()
                }
            })
}

fun View.hideWithTranslationAnimation(endCallback: () -> (Unit)) {
    alpha = 1.0f
    translationY = 0f
    val endTranslationY = height.toFloat()
    animate()
            .setDuration(ANIMATION_DURATION)
            .alpha(0.0f)
            .translationY(endTranslationY)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    translationY = endTranslationY
                    hide()
                    alpha = 1.0f
                    endCallback.invoke()
                }
            })
}

const val ANIMATION_DURATION: Long = 250