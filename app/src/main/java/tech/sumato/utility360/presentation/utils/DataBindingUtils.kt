package tech.sumato.utility360.presentation.utils

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

@set:BindingAdapter("visible")
var View.visible
    get() = this.isVisible
    set(value) {
        this.isVisible = value
    }

@set:BindingAdapter("imageUrl")
var AppCompatImageView.imageUrl
    get() = ""
    set(value) {
        Glide.with(this)
            .asBitmap()
            .load(value)
            .into(this)
    }


@set:BindingAdapter("drawableResource")
var AppCompatImageView.drawableResource
    get() = 0
    set(value) {
        setImageResource(value)
    }


@set:BindingAdapter("buttonIcon")
var MaterialButton.buttonIcon
    get() = 0
    set(value) {
        icon = ContextCompat.getDrawable(context, value)
    }