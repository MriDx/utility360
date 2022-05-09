package tech.sumato.utility360.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf


fun Context.startActivity(activity: Class<*>, bundle: (() -> Bundle)? = null) {
    startActivity(Intent(this, activity).apply {
        bundle ?: return@apply
        putExtras(bundle.invoke())
    })
}

