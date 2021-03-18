package com.foobarust.deliverer.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * Created by kevin on 2/17/21
 */

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
    // Use local drawable if the given url is null.
    if (imageUrl == null) {
        loadGlideSrc(placeholder, centerCrop, circularCrop)
        return
    }

    createGlideRequest(
        context,
        imageUrl,
        centerCrop,
        circularCrop,
        placeholder
    ).into(this)
}

/**
 * Set the [ImageView] by loading an image using a [Drawable] resource.
 * @param drawableRes the resource id of the drawable.
 * @param centerCrop whether to apply center cropping the image.
 * @param circularCrop whether to apply circular cropping to the image.
 */
fun ImageView.loadGlideSrc(
    @DrawableRes drawableRes: Int?,
    centerCrop: Boolean = false,
    circularCrop: Boolean = false
) {
    if (drawableRes == null) return

    createGlideRequest(
        context,
        drawableRes,
        centerCrop,
        circularCrop
    ).into(this)
}

private fun createGlideRequest(
    context: Context,
    @DrawableRes drawableRes: Int,
    centerCrop: Boolean,
    circularCrop: Boolean
): RequestBuilder<Drawable> {
    val req = Glide.with(context).load(drawableRes)
        .transition(DrawableTransitionOptions.withCrossFade())

    if (centerCrop) req.centerCrop()
    if (circularCrop) req.circleCrop()

    return req
}

private fun createGlideRequest(
    context: Context,
    imageUrl: String,
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