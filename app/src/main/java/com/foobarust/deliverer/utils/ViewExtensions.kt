package com.foobarust.deliverer.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.progressindicator.LinearProgressIndicator

/**
 * Created by kevin on 2/17/21
 */

fun LinearProgressIndicator.hideIf(hide: Boolean) {
    if (hide) hide() else show()
}

fun TextView.drawableFitVertical() {
    val drawableSize = lineHeight
    val updatedDrawables = compoundDrawablesRelative.map { drawable: Drawable? ->
        drawable?.setBounds(0, 0, drawableSize, drawableSize)
        drawable
    }

    setCompoundDrawablesRelative(
        updatedDrawables[0],        /* left */
        updatedDrawables[1],        /* top */
        updatedDrawables[2],        /* right */
        updatedDrawables[3]         /* bottom */
    )
}

fun TextView.setDrawables(
    @DrawableRes drawableLeft: Int? = null,
    @DrawableRes drawableTop: Int? = null,
    @DrawableRes drawableRight: Int? = null,
    @DrawableRes drawableBottom: Int? = null
) {
    setCompoundDrawablesWithIntrinsicBounds(
        context.getDrawableOrNull(drawableLeft),
        context.getDrawableOrNull(drawableTop),
        context.getDrawableOrNull(drawableRight),
        context.getDrawableOrNull(drawableBottom)
    )
}

/**
 * Set the [ImageView] using a [Drawable] resource.
 * @param drawableRes the resource id of the drawable.
 */
fun ImageView.setSrc(
    @DrawableRes drawableRes: Int?
) {
    if (drawableRes == null) return
    val drawable = context.getDrawableOrNull(drawableRes)
    drawable?.let { setImageDrawable(it) }
}

/**
 * Set the [ImageView] by loading an image using a given url.
 * @param imageUrl the url of the image.
 * @param centerCrop whether to apply center cropping the image.
 * @param circularCrop whether to apply circular cropping to the image.
 * @param placeholder the resource id of the fallback drawable if there is
 * network error.
 */
fun ImageView.loadGlideUrl(
    imageUrl: String?,
    centerCrop: Boolean = false,
    circularCrop: Boolean = false,
    @DrawableRes placeholder: Int? = null
) {
    createGlideRequest(
        context,
        imageUrl,
        centerCrop,
        circularCrop,
        placeholder
    ).into(this)
}

private fun createGlideRequest(
    context: Context,
    imageUrl: String?,
    centerCrop: Boolean,
    circularCrop: Boolean,
    placeholder: Int?
): RequestBuilder<Drawable> {
    val req = Glide.with(context).load(imageUrl)
        .transition(DrawableTransitionOptions.withCrossFade())

    if (placeholder != null) req.placeholder(context.getDrawableOrNull(placeholder))
    if (centerCrop) req.centerCrop()
    if (circularCrop) req.circleCrop()

    return req
}

fun View.applyLayoutFullscreen() {
    systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}

fun View.applySystemWindowInsetsMargin(
    applyLeft: Boolean = false,
    applyTop: Boolean = false,
    applyRight: Boolean = false,
    applyBottom: Boolean = false
) {
    doOnApplyWindowInsets { view, insets, _, margin, _ ->
        val systemWindowInsets = WindowInsetsCompat.toWindowInsetsCompat(insets)
            .getInsets(WindowInsetsCompat.Type.systemBars())

        val left = if (applyLeft) systemWindowInsets.left else 0
        val top = if (applyTop) systemWindowInsets.top else 0
        val right = if (applyRight) systemWindowInsets.right else 0
        val bottom = if (applyBottom) systemWindowInsets.bottom else 0

        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = margin.left + left
            topMargin = margin.top + top
            rightMargin = margin.right + right
            bottomMargin = margin.bottom + bottom
        }
    }
}

fun View.applySystemWindowInsetsPadding(
    applyLeft: Boolean = false,
    applyTop: Boolean = false,
    applyRight: Boolean = false,
    applyBottom: Boolean = false
) {
    doOnApplyWindowInsets { view, insets, padding, _, _ ->
        val systemWindowInsets = WindowInsetsCompat.toWindowInsetsCompat(insets)
            .getInsets(WindowInsetsCompat.Type.systemBars())

        val left = if (applyLeft) systemWindowInsets.left else 0
        val top = if (applyTop) systemWindowInsets.top else 0
        val right = if (applyRight) systemWindowInsets.right else 0
        val bottom = if (applyBottom) systemWindowInsets.bottom else 0

        view.setPadding(
            padding.left + left,
            padding.top + top,
            padding.right + right,
            padding.bottom + bottom
        )
    }
}

fun View.doOnApplyWindowInsets(
    block: (View, WindowInsets, InitialPadding, InitialMargin, Int) -> Unit
) {
    // Create a snapshot of the view's padding & margin states
    val initialPadding = recordInitialPaddingForView(this)
    val initialMargin = recordInitialMarginForView(this)
    val initialHeight = recordInitialHeightForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding & margin states
    setOnApplyWindowInsetsListener { v, insets ->
        block(v, insets, initialPadding, initialMargin, initialHeight)
        // Always return the insets, so that children can also use them
        insets
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

private fun recordInitialMarginForView(view: View): InitialMargin {
    val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        ?: throw IllegalArgumentException("Invalid view layout params")
    return InitialMargin(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
}

private fun recordInitialHeightForView(view: View): Int {
    return view.layoutParams.height
}

class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

class InitialMargin(val left: Int, val top: Int, val right: Int, val bottom: Int)