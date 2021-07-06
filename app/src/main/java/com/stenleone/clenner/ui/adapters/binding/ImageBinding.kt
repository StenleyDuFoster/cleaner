package com.stenleone.clenner.ui.adapters.binding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.stenleone.clenner.util.glide.GlideApp

@BindingAdapter("app:loadImageByDrawable")
fun loadImageByDrawable(imageView: ImageView, drawable: Drawable) {

    GlideApp.with(imageView.context)
        .load(drawable)
        .into(imageView)
}