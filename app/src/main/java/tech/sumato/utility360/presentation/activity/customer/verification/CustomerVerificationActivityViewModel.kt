package tech.sumato.utility360.presentation.activity.customer.verification

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import org.json.JSONObject
import tech.sumato.utility360.data.remote.model.tasks.SiteVerificationTaskRequest
import tech.sumato.utility360.data.remote.utils.Status
import tech.sumato.utility360.data.remote.web_service.source.customer.CustomerDataSource
import tech.sumato.utility360.data.remote.web_service.source.tasks.PendingSiteVerificationDataSource
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.domain.use_case.customer.GetCustomersWithDocumentUseCase
import tech.sumato.utility360.domain.use_case.firebase.FirebaseImageUploadUseCase
import tech.sumato.utility360.domain.use_case.location.EnableGpsUseCase
import tech.sumato.utility360.domain.use_case.location.GpsResult
import tech.sumato.utility360.domain.use_case.location.LocationUpdatesUseCase
import tech.sumato.utility360.domain.use_case.tasks.GetPendingSiteVerificationsUseCase
import tech.sumato.utility360.domain.use_case.tasks.SubmitSiteVerificationUseCase
import tech.sumato.utility360.presentation.fragments.base.listing.ListingViewModel
import tech.sumato.utility360.presentation.utils.Navigation
import tech.sumato.utility360.presentation.utils.PostSubmitProcessViewModel
import javax.inject.Inject

@HiltViewModel
class CustomerVerificationActivityViewModel
@Inject constructor(
    private val getCustomersWithDocumentUseCase: GetCustomersWithDocumentUseCase,
    private val getPendingSiteVerificationsUseCase: GetPendingSiteVerificationsUseCase,
    private val locationUpdatesUseCase: LocationUpdatesUseCase,
    private val enableGpsUseCase: EnableGpsUseCase,
    private val firebaseImageUploadUseCase: FirebaseImageUploadUseCase,
    private val submitSiteVerificationUseCase: SubmitSiteVerificationUseCase,
) : ListingViewModel(), Navigation {


    private var navigation_ = MutableSharedFlow<FragmentNavigation>()
    val navigation: SharedFlow<FragmentNavigation> = navigation_

    val gpsResultFlow = MutableSharedFlow<GpsResult>()

    private var currentLocation: Location? = null

    private var siteVerificationTaskRequestObject: SiteVerificationTaskRequest? = null

    var jobInProgress: Boolean = false
    var jobSuccess: Boolean = false


    fun getCustomers(query: MutableMap<String, String> = mutableMapOf()) = Pager(
        config = PagingConfig(pageSize = 2, prefetchDistance = 2),
        pagingSourceFactory = {
            CustomerDataSource(getCustomersWithDocumentUseCase, query = query)
        })
        .flow
        .cachedIn(viewModelScope)

    fun getPendingSiteVerifications() = Pager(
        config = PagingConfig(pageSize = 2, prefetchDistance = 2),
        pagingSourceFactory = {
            PendingSiteVerificationDataSource(
                getPendingSiteVerificationsUseCase = getPendingSiteVerificationsUseCase
            )
        })
        .flow
        .cachedIn(viewModelScope)


    override fun navigate(fragment: Class<*>) {
        super.navigate(fragment)
        viewModelScope.launch {
            val tmpFragment = (fragment.newInstance() as? Fragment)
                ?: throw IllegalArgumentException("Pass a fragment instance to navigate")
            navigation_.emit(
                FragmentNavigation(
                    fragment = tmpFragment,
                    args = null
                )
            )
        }
    }

    override fun navigate(fragment: Class<*>, args: Bundle) {
        viewModelScope.launch {
            val tmpFragment = (fragment.newInstance() as? Fragment)
                ?: throw IllegalArgumentException("Pass a fragment instance to navigate")
            navigation_.emit(
                FragmentNavigation(
                    fragment = tmpFragment,
                    args = args
                )
            )
        }
    }


    fun submitVerification(paramsObject: SiteVerificationTaskRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            currentLocation ?: return@launch

            siteVerificationTaskRequestObject = paramsObject

            paramsObject.customer_location =
                "${currentLocation!!.latitude}, ${currentLocation!!.longitude}"

            submission()


        }
    }


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


    private var pendingJob: Job? = null


    suspend fun submission() {
        pendingJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                //
                jobSuccess = false
                jobInProgress = true
                notifyInProgress()
                //delay(1000 *3)
                val siteVerificationRequest = siteVerificationTaskRequestObject!!

                val uploadResponse =
                    firebaseImageUploadUseCase(imagePath = siteVerificationRequest.uploadableImagePath)

                Log.d("mridx", "submission: image upload response $uploadResponse")

                if (uploadResponse.isNullOrEmpty()) cancel("image upload failed !")

                //after successful upload post to server

                siteVerificationRequest.photo = uploadResponse!!


                val params = siteVerificationRequest.toJson()

                Log.d("mridx", "submission: upload params $params")

                val response = submitSiteVerificationUseCase(
                    customerUuid = siteVerificationRequest.customerUuid,
                    requestParams = params
                )

                if (response.isFailed()) {
                    throw Exception("Request failed !")
                }

                jobInProgress = false
                jobSuccess = true

                notifyJobComplete(
                    status = Status.SUCCESS,
                    message = "Your request has been completed successfully "
                )


            } catch (e: Exception) {
                e.printStackTrace()
                jobInProgress = false
                jobSuccess = false
                notifyJobComplete(
                    status = Status.FAILED,
                    message = "Your request failed for ${e.message} "
                )
            }
        }
    }


}