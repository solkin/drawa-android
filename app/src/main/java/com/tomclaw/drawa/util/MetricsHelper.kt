package com.tomclaw.drawa.util

import android.content.res.Resources
import android.util.DisplayMetrics

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
 * @return A float value to represent px equivalent to dp depending on device density
 */
fun Resources.convertDpToPixel(dp: Float): Float {
    val metrics = displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @param px A value in px (pixels) unit. Which we need to convert into db
 * @return A float value to represent dp equivalent to px value
 */
fun Resources.convertPixelsToDp(px: Float): Float {
    val metrics = displayMetrics
    return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}