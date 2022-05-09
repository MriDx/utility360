package tech.sumato.utility360.presentation.fragments.tasks

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tech.sumato.utility360.presentation.fragments.base.listing.ListingViewModel
import javax.inject.Inject

@HiltViewModel
class TasksFragmentViewModel @Inject constructor() : ListingViewModel() {


    init {
        setUIError(UIError.show(message = "Is it working ??"))
        removeError()
    }

    private fun removeError() {
        viewModelScope.launch {
            delay(1000 * 2)
            setUIError(UIError.hide())
        }
    }


}