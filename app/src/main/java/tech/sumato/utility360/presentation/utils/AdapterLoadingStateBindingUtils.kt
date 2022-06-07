package tech.sumato.utility360.presentation.utils

import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.paging.LoadState
import tech.sumato.utility360.utils.parseException

@BindingAdapter("errorState")
fun TextView.errorState(state: LoadState) {
    val exception = (state as? LoadState.Error)?.error ?: return
    isVisible = !exception.message.isNullOrEmpty()
    text = parseException(e = exception)
    //text = exception?.message
}