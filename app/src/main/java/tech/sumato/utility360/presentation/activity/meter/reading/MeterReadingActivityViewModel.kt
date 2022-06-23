package tech.sumato.utility360.presentation.activity.meter.reading

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.tasks.MeterReadingTaskRequest
import tech.sumato.utility360.data.remote.utils.Status
import tech.sumato.utility360.data.remote.web_service.source.customer.CustomerDataSource
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.domain.use_case.customer.GetCustomerUseCase
import tech.sumato.utility360.domain.use_case.customer.GetCustomersWithDocumentUseCase
import tech.sumato.utility360.domain.use_case.firebase.FirebaseImageUploaderUseCase
import tech.sumato.utility360.domain.use_case.location.EnableGpsUseCase
import tech.sumato.utility360.domain.use_case.location.GnssStatusListenerUseCase
import tech.sumato.utility360.domain.use_case.location.GpsResult
import tech.sumato.utility360.domain.use_case.location.LocationUpdatesUseCase
import tech.sumato.utility360.domain.use_case.tasks.SubmitMeterReadingUseCase
import tech.sumato.utility360.presentation.fragments.base.listing.ListingViewModel
import tech.sumato.utility360.presentation.fragments.customer.find.FindCustomerFragment
import tech.sumato.utility360.presentation.utils.Navigation
import tech.sumato.utility360.presentation.utils.PostSubmitProcessViewModel
import tech.sumato.utility360.utils.METER_READING_METER_IMAGE
import tech.sumato.utility360.utils.NotInUse
import tech.sumato.utility360.utils.NotWorking
import javax.inject.Inject

@HiltViewModel
class MeterReadingActivityViewModel @Inject constructor(

    private val locationUpdatesUseCase: LocationUpdatesUseCase,
    private val enableGpsUseCase: EnableGpsUseCase,
    private val gnssStatusListenerUseCase: GnssStatusListenerUseCase,
    private val getCustomersWithDocumentUseCase: GetCustomersWithDocumentUseCase,
    private val firebaseImageUploaderUseCase: FirebaseImageUploaderUseCase,
    private val submitMeterReadingUseCase: SubmitMeterReadingUseCase,
    private val getCustomerUseCase: GetCustomerUseCase,

    ) : ListingViewModel(), Navigation {


    private var navigation_ = MutableSharedFlow<FragmentNavigation>()
    val navigation: SharedFlow<FragmentNavigation> = navigation_

    val gpsResultFlow = MutableSharedFlow<GpsResult>()

    private var currentLocation: Location? = null


    private var lastMeterReadingFlow_ = MutableSharedFlow<CustomerResource>()
    val lastMeterReadingFlow: SharedFlow<CustomerResource> = lastMeterReadingFlow_


    var jobInProgress = false
    var pendingJob: Job? = null
    var jobSuccess: Boolean = false
    var meterReadingTaskRequestObject: MeterReadingTaskRequest? = null


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


    //endregion


    var searchedCustomer: MutableSharedFlow<PagingData<CustomerResource>> = MutableSharedFlow()

    fun doSearchByQuery(searchQuery: String) {

        viewModelScope.launch {
            val result = getCustomers(
                query = mutableMapOf(
                    "filter[search_by]" to searchQuery,
                    //"include" to "user.lastMeterReading",
                    "filter[connection_status]" to "active",
                    "include" to "customerCheck,meterInstallation"
                )
            ).cachedIn(viewModelScope)
            searchedCustomer.emitAll(result)
        }

    }

    fun getCustomers(query: MutableMap<String, String> = mutableMapOf()) = Pager(
        config = PagingConfig(pageSize = 2, prefetchDistance = 2),
        pagingSourceFactory = {
            CustomerDataSource(getCustomersWithDocumentUseCase, query = query)
        })
        .flow


    fun submitMeterReading(meterReadingTaskRequest: MeterReadingTaskRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val tmpCurrentLocation = currentLocation

            meterReadingTaskRequestObject = meterReadingTaskRequest

            tmpCurrentLocation ?: return@launch

            submission()

        }
    }

    private fun submission() {
        pendingJob = viewModelScope.launch(Dispatchers.IO) {

            try {
                jobInProgress = true
                jobSuccess = false

                notifyInProgress()

                val tmpData = meterReadingTaskRequestObject!!
                tmpData.lat_long = "${currentLocation?.latitude},${currentLocation?.longitude}"

                //delay(1000 * 3)

                val uploadedMeterImage = firebaseImageUploaderUseCase(
                    imagePath = tmpData.uploadableImagePath,
                    uploadType = METER_READING_METER_IMAGE
                )

                if (uploadedMeterImage.isNullOrEmpty()) throw Exception("meter image uploading failed !")

                tmpData.meter_image = uploadedMeterImage

                //after successfully uploads images

                val params = tmpData.toJson()

                //Log.d("mridx", "submission: meter reading submit params - $params")

                val response = submitMeterReadingUseCase(
                    customerUuid = tmpData.customerUuid,
                    params = params
                )

                if (response.isFailed()) {
                    //
                    if (response.message != null) {
                        throw Exception(response.message)
                    }
                    throw Exception("request failed !")
                }



                jobInProgress = false
                jobSuccess = true

                notifyJobComplete(
                    status = Status.SUCCESS,
                    message = "Your request has been completed successfully "
                )

            } catch (e: Exception) {
                e.printStackTrace()
                //cancel(e.message.toString())
                jobInProgress = false
                jobSuccess = false
                notifyJobComplete(
                    status = Status.FAILED,
                    message = "Your request failed for ${e.message} "
                )
            }

        }
    }

    fun fetchLastMeterReading(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = getCustomerUseCase(
                uuid, mapOf(
                    "include" to "user.lastMeterReading"
                )
            )

            if (response.isFailed()) {
                //failed to fetch last meter reading
                return@launch
            }

            //last meter reading fetched, notify ui
            lastMeterReadingFlow_.emit(response.data!!.data!!)


        }
    }

}