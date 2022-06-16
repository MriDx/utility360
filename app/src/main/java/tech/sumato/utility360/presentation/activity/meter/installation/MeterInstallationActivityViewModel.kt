package tech.sumato.utility360.presentation.activity.meter.installation

import android.os.Bundle
import android.util.Log
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import tech.sumato.utility360.data.local.entity.MeterInstallationEntity
import tech.sumato.utility360.data.remote.model.tasks.MeterInstallationTaskRequest
import tech.sumato.utility360.data.remote.utils.Status
import tech.sumato.utility360.data.remote.web_service.source.customer.CustomerDataSource
import tech.sumato.utility360.data.remote.web_service.source.tasks.PendingMeterInstallationDataSource
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.domain.use_case.customer.GetCustomersWithDocumentUseCase
import tech.sumato.utility360.domain.use_case.firebase.FirebaseImageUploaderUseCase
import tech.sumato.utility360.domain.use_case.tasks.GetPendingMeterInstallationsUseCase
import tech.sumato.utility360.domain.use_case.tasks.MeterQRAssociationUseCase
import tech.sumato.utility360.domain.use_case.tasks.SubmitMeterInstallationUseCase
import tech.sumato.utility360.presentation.fragments.base.listing.ListingViewModel
import tech.sumato.utility360.presentation.utils.Navigation
import tech.sumato.utility360.utils.METER_INSTALLATION_METER_IMAGE
import tech.sumato.utility360.utils.METER_INSTALLATION_SITE_IMAGE
import tech.sumato.utility360.utils.parseException
import javax.inject.Inject

@HiltViewModel
class MeterInstallationActivityViewModel @Inject constructor(
    private val getCustomersWithDocumentUseCase: GetCustomersWithDocumentUseCase,
    private val firebaseImageUploaderUseCase: FirebaseImageUploaderUseCase,
    private val submitMeterInstallationUseCase: SubmitMeterInstallationUseCase,
    private val getPendingMeterInstallationsUseCase: GetPendingMeterInstallationsUseCase,
    private val meterQRAssociationUseCase: MeterQRAssociationUseCase,
) : ListingViewModel(), Navigation {

    private var navigation_ = MutableSharedFlow<FragmentNavigation>()
    val navigation: SharedFlow<FragmentNavigation> = navigation_


    private var pendingJob: Job? = null

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


    @Deprecated("use  the live one")
    fun emulateMeterInstallationSubmission() {
        pendingJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                jobInProgress = true
                jobSuccess = false
                notifyInProgress()
                delay(1000 * 3)

                jobInProgress = false
                jobSuccess = true

                /*notifyJobComplete(
                    status = Status.FAILED,
                    message = "Your request has been completed successfully but Meter QR Association is failed.",
                )*/

                notifyJobCompletedWithException(
                    status = Status.SUCCESS,
                    message = "Your request has been completed successfully but Meter QR Association is failed.",
                    primaryBtn = "Retry QR Association"
                )
            } catch (e: Exception) {
                e.printStackTrace()

            }
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


                val qrAssociationResponse = meterQRAssociationUseCase(
                    customerUuid = meterInstallationTaskRequest.customerUuid,
                    params = meterInstallationTaskRequest.getScannedQRJson()
                )

                //422 Unprocessable Content http://pbg-test.sumato.tech/api/v1/customers/c3aeaa6e-a942-4556-9e79-b66921c3cbbf/qrcode (140ms)
                //{"message":"The selected qr code is invalid.","errors":{"qr_code":["The selected qr code is invalid."]}}

                if (qrAssociationResponse.isFailed()) {
                    jobInProgress = false
                    jobSuccess = true

                    notifyJobCompletedWithException(
                        status = Status.SUCCESS,
                        message = "Meter installation has been completed successfully but Meter QR Association is failed ${if (qrAssociationResponse.message != null) " because of ${qrAssociationResponse.message}" else ""}.",
                        primaryBtn = "Retry QR Association"
                    )

                    return@launch
                } else {

                    jobInProgress = false
                    jobSuccess = true


                    notifyJobComplete(
                        status = Status.SUCCESS,
                        message = "Your request has been completed successfully "
                    )
                }


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

    fun getCustomers(query: MutableMap<String, String> = mutableMapOf()) = Pager(
        config = PagingConfig(pageSize = 2, prefetchDistance = 2),
        pagingSourceFactory = {
            CustomerDataSource(getCustomersWithDocumentUseCase, query = query)
        })
        .flow
        .cachedIn(viewModelScope)


    //region meter qr asscoation emulation
    fun emulateMeterQrSubmit(qrData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            emulateMeterQr(qrData)
        }
    }

    var emulateRetries = 1
    fun emulateMeterQr(qrData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                jobInProgress = true
                jobSuccess = false

                notifyInProgress()

                val toPost = JSONObject().apply {
                    put("qr_code", qrData)
                }


                delay(1000 * 3)

                Log.d("mridx", "emulateMeterQr: $toPost")


                if (emulateRetries == 3) {
                    jobInProgress = false
                    jobSuccess = true


                    notifyJobComplete(
                        status = Status.SUCCESS,
                        message = "Your request has been completed successfully "
                    )
                } else {
                    jobInProgress = false
                    jobSuccess = false

                    notifyJobCompletedWithException(
                        status = Status.SUCCESS,
                        message = "Meter QR Association is failed .",
                        primaryBtn = "Retry QR Association"
                    )
                }

                emulateRetries++


            } catch (e: Exception) {
                e.printStackTrace()
                cancel(e.message.toString())
                jobInProgress = false
                jobSuccess = false
                emulateRetries++
                notifyJobComplete(
                    status = Status.FAILED,
                    message = "Your request failed for ${e.message} "
                )
            }


        }
    }

    //endregion

    fun submitMeterQr(qrData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            submitMeterQrImpl(qrData)
        }
    }

    fun submitMeterQrImpl(qrData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                jobInProgress = true
                jobSuccess = false
                notifyInProgress()

                val toPost = JSONObject().apply {
                    put("qr_code", qrData)
                }

                val meterInstallationTaskRequest = meterInstallationTaskRequestObject!!

                val qrAssociationResponse = meterQRAssociationUseCase(
                    customerUuid = meterInstallationTaskRequest.customerUuid,
                    params = toPost
                )

                if (qrAssociationResponse.isFailed()) {
                    jobInProgress = false
                    jobSuccess = false

                    notifyJobCompletedWithException(
                        status = Status.SUCCESS,
                        message = "Meter QR Association is failed ${if (qrAssociationResponse.message != null) " because of ${qrAssociationResponse.message}" else ""}.",
                        primaryBtn = "Retry QR Association"
                    )
                } else {

                    jobInProgress = false
                    jobSuccess = true


                    notifyJobComplete(
                        status = Status.SUCCESS,
                        message = "Your request has been completed successfully "
                    )
                }


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


}