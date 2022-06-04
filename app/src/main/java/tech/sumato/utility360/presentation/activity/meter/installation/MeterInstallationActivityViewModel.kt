package tech.sumato.utility360.presentation.activity.meter.installation

import android.os.Bundle
import android.util.Log
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import tech.sumato.utility360.data.local.entity.MeterInstallationEntity
import tech.sumato.utility360.data.remote.model.tasks.MeterInstallationTaskRequest
import tech.sumato.utility360.data.remote.utils.Status
import tech.sumato.utility360.data.remote.web_service.source.customer.CustomerDataSource
import tech.sumato.utility360.data.remote.web_service.source.tasks.PendingMeterInstallationDataSource
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.domain.use_case.customer.GetCustomersWithDocumentUseCase
import tech.sumato.utility360.domain.use_case.firebase.FirebaseImageUploaderUseCase
import tech.sumato.utility360.domain.use_case.tasks.GetPendingMeterInstallationsUseCase
import tech.sumato.utility360.domain.use_case.tasks.SubmitMeterInstallationUseCase
import tech.sumato.utility360.presentation.fragments.base.listing.ListingViewModel
import tech.sumato.utility360.presentation.utils.Navigation
import tech.sumato.utility360.utils.METER_INSTALLATION_METER_IMAGE
import tech.sumato.utility360.utils.METER_INSTALLATION_SITE_IMAGE
import javax.inject.Inject

@HiltViewModel
class MeterInstallationActivityViewModel @Inject constructor(
    private val getCustomersWithDocumentUseCase: GetCustomersWithDocumentUseCase,
    private val firebaseImageUploaderUseCase: FirebaseImageUploaderUseCase,
    private val submitMeterInstallationUseCase: SubmitMeterInstallationUseCase,
    private val getPendingMeterInstallationsUseCase: GetPendingMeterInstallationsUseCase,
) : ListingViewModel(),
    Navigation {

    private var navigation_ = MutableSharedFlow<FragmentNavigation>()
    val navigation: SharedFlow<FragmentNavigation> = navigation_

    var meterInstallationEntityObserver: ObservableField<MeterInstallationEntity> =
        ObservableField(MeterInstallationEntity())


    private var pendingJob: Job? = null

    private var retries = 0

    var jobSuccess = false

    /**
     * starts pending job if any
     */
    fun startPendingJob() {
        pendingJob?.start()
    }


    override fun navigate(fragment: Class<*>) {
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


    private var meterInstallationTaskRequestObject: MeterInstallationTaskRequest? = null
    fun submitMeterInstallation(meterInstallationTaskRequest: MeterInstallationTaskRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            meterInstallationTaskRequestObject = meterInstallationTaskRequest

            submission()

        }
    }

    var jobInProgress = false
    private fun submission() {
        pendingJob = viewModelScope.launch(Dispatchers.IO) {

            try {

                jobInProgress = true
                jobSuccess = false
                notifyInProgress()

                val meterInstallationTaskRequest = meterInstallationTaskRequestObject!!

                val uploadMeterImage = firebaseImageUploaderUseCase(
                    imagePath = meterInstallationTaskRequest.uploadableMeterPhoto,
                    uploadType = METER_INSTALLATION_METER_IMAGE
                )

                if (uploadMeterImage.isNullOrEmpty()) throw Exception("image uploading failed")

                val uploadSiteImage = firebaseImageUploaderUseCase(
                    imagePath = meterInstallationTaskRequest.uploadableSitePhoto,
                    uploadType = METER_INSTALLATION_SITE_IMAGE
                )

                if (uploadSiteImage.isNullOrEmpty()) throw Exception("image uploading failed")

                meterInstallationTaskRequest.initial_meter_photo = uploadMeterImage
                meterInstallationTaskRequest.after_installation_photo = uploadSiteImage

                //after successfully uploads images

                val params = meterInstallationTaskRequest.toJson()

                Log.d("mridx", "submission: upload params $params")

                val response = submitMeterInstallationUseCase(
                    customerUuid = meterInstallationTaskRequest.customerUuid,
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
                cancel(e.message.toString())
                jobInProgress = false
                jobSuccess = false
                notifyJobComplete(
                    status = Status.FAILED,
                    message = "Your request failed for ${e.message} "
                )
            }

        }

    }



    fun getPendingMeterInstallations() = Pager(
        config = PagingConfig(pageSize = 2, prefetchDistance = 2),
        pagingSourceFactory = {
            PendingMeterInstallationDataSource(getPendingMeterInstallationsUseCase)
        })
        .flow
        .cachedIn(viewModelScope)


}