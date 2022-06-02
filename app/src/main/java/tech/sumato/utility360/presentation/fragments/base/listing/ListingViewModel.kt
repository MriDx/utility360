package tech.sumato.utility360.presentation.fragments.base.listing

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import tech.sumato.utility360.data.local.entity.utils.UIError

open class ListingViewModel : ViewModel() {


    var uiError: ObservableField<UIError> = ObservableField(UIError.hide())

    open fun setUIError(uiError: UIError) {
        this.uiError.set(uiError)
        this.uiError.notifyChange()
    }


}