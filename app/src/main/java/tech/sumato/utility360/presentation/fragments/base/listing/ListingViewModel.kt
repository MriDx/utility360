package tech.sumato.utility360.presentation.fragments.base.listing

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

open class ListingViewModel : ViewModel() {

    data class UIError(
        val showError: Boolean = false,
        val errorMessage: String? = null,
    ) {
        companion object {
            fun show(message: String): UIError {
                return UIError(showError = true, errorMessage = message)
            }

            fun hide(): UIError {
                return UIError(showError = false)
            }
        }
    }

    var uiError: ObservableField<UIError> = ObservableField(UIError.hide())

    open fun setUIError(uiError: UIError) {
        this.uiError.set(uiError)
        this.uiError.notifyChange()
    }


}