package tech.sumato.utility360.presentation.activity.meter.reading

import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.remote.web_service.source.customer.CustomerDataSource
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.domain.use_case.customer.GetCustomersWithDocumentUseCase
import tech.sumato.utility360.domain.use_case.location.EnableGpsUseCase
import tech.sumato.utility360.domain.use_case.location.GnssStatusListenerUseCase
import tech.sumato.utility360.domain.use_case.location.GpsResult
import tech.sumato.utility360.domain.use_case.location.LocationUpdatesUseCase
import tech.sumato.utility360.presentation.fragments.base.listing.ListingViewModel
import tech.sumato.utility360.presentation.fragments.customer.find.FindCustomerFragment
import tech.sumato.utility360.presentation.utils.Navigation
import tech.sumato.utility360.utils.NotInUse
import tech.sumato.utility360.utils.NotWorking
import javax.inject.Inject

@HiltViewModel
class MeterReadingActivityViewModel @Inject constructor(

    private val locationUpdatesUseCase: LocationUpdatesUseCase,
    private val enableGpsUseCase: EnableGpsUseCase,
    private val gnssStatusListenerUseCase: GnssStatusListenerUseCase,
    private val getCustomersWithDocumentUseCase: GetCustomersWithDocumentUseCase,

    ) : ListingViewModel(), Navigation {


    private var navigation_ = MutableSharedFlow<FragmentNavigation>()
    val navigation: SharedFlow<FragmentNavigation> = navigation_

    val gpsResultFlow = MutableSharedFlow<GpsResult>()

    private var currentLocation: Location? = null

    /**
     * checks if gps enable and if not
     * return its exception for resolution
     *
     */
    fun enableGps() {
        viewModelScope.launch {
            enableGpsUseCase()
                .collect { gpsResult ->

                    if (gpsResult.enabled) {
                        //
                        locationUpdates()
                        return@collect
                    }
                    gpsResultFlow.emit(gpsResult)
                }
        }
    }

    /**
     * Listens for location changes
     *
     */
    fun locationUpdates() {
        viewModelScope.launch {
            locationUpdatesUseCase
                .fetchUpdates()
                .collectLatest {
                    currentLocation = it
                }

        }
    }


    @NotInUse
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

    override fun navigate(fragment: Class<*>) {
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

    override fun navigate(fragment: Class<*>, args: Bundle) {
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


    //region gnss status callback

    @NotWorking(reason = "Gnss status callback not working")
    @Deprecated("")
    private fun gpsListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gnssListener()
            return
        }
        enableGps()
    }

    @NotWorking(reason = "Gnss status callback not working")
    @Deprecated("")
    private fun gnssListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewModelScope.launch {
                gnssStatusListenerUseCase()
                    .collectLatest { enabled ->
                        if (!enabled) {
                            enableGps()
                            return@collectLatest
                        }
                    }
            }
        }

    }

    fun submitMeterReading() {
        viewModelScope.launch {
            val tmpCurrentLocation = currentLocation
            val latitude = tmpCurrentLocation?.latitude
            val longitude = tmpCurrentLocation?.longitude
        }
    }


    //endregion


    fun getCustomers(query: MutableMap<String, String> = mutableMapOf()) = Pager(
        config = PagingConfig(pageSize = 2, prefetchDistance = 2),
        pagingSourceFactory = {
            CustomerDataSource(getCustomersWithDocumentUseCase, query = query)
        })
        .flow
        .cachedIn(viewModelScope)

}