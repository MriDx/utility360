package tech.sumato.utility360.presentation.activity.meter.reading

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import tech.sumato.utility360.presentation.fragments.customer.find.FindCustomerFragment
import tech.sumato.utility360.presentation.fragments.meter.image.MeterImageFragment
import javax.inject.Inject

@HiltViewModel
class MeterReadingActivityViewModel @Inject constructor() : ViewModel() {

    data class FragmentNavigation(
        val fragment: Fragment,
        val args: Bundle? = null
    )

    private var navigation_ = MutableSharedFlow<FragmentNavigation>()
    val navigation: SharedFlow<FragmentNavigation> = navigation_


    init {
        //addCustomerFinderFragment()
    }

    private fun addCustomerFinderFragment() {
        viewModelScope.launch {
            val fragment = FindCustomerFragment::class.java
            navigation_.emit(
                FragmentNavigation(
                    fragment = fragment.newInstance(), null
                )
            )
        }
    }

    fun navigate(fragment: Class<*>) {
        /*if (!fragment.isInstance(Fragment::class.java)) {
            throw IllegalArgumentException("Pass a fragment instance to navigate")
        }*/

        val tmpFragment = (fragment.newInstance() as? Fragment)
            ?: throw IllegalArgumentException("Pass a fragment instance to navigate")

        viewModelScope.launch {
            navigation_.emit(
                FragmentNavigation(
                    fragment = tmpFragment,
                    null
                )
            )
        }

    }

    fun navigate(fragment: Class<*>, args: Bundle) {
        /*if (!fragment.isInstance(Fragment::class.java)) {
            throw IllegalArgumentException("Pass a fragment instance to navigate")
        }*/

        val tmpFragment = (fragment.newInstance() as? Fragment)
            ?: throw IllegalArgumentException("Pass a fragment instance to navigate")

        viewModelScope.launch {
            navigation_.emit(
                FragmentNavigation(
                    fragment = tmpFragment,
                    args
                )
            )
        }

    }


}