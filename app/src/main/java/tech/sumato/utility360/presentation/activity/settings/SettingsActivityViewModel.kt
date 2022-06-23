package tech.sumato.utility360.presentation.activity.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.remote.model.user.ChangePasswordRequest
import tech.sumato.utility360.data.remote.model.user.SettingsRequest
import tech.sumato.utility360.data.remote.utils.Status
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.domain.use_case.user.ChangePasswordUseCase
import tech.sumato.utility360.presentation.utils.Navigation
import tech.sumato.utility360.presentation.utils.PostSubmitProcessViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsActivityViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : PostSubmitProcessViewModel(), Navigation {


    private var navigation_ = MutableSharedFlow<FragmentNavigation>()
    val navigation: SharedFlow<FragmentNavigation> = navigation_


    private var pendingJob: Job? = null
    var jobSuccess = false
    var jobInProgress = false


    fun startPendingJob() {
        pendingJob?.start()
    }

    fun cancelCurrentJob() {
        pendingJob?.cancel()
    }

    private var requestData: SettingsRequest? = null


    fun changePassword(passwordChangeRequest: ChangePasswordRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            requestData = passwordChangeRequest
            changePasswordImpl()
        }
    }

    private fun changePasswordImpl() {
        pendingJob = viewModelScope.launch(Dispatchers.IO) {

            try {
                jobInProgress = true
                jobSuccess = false

                notifyInProgress()

                val passwordChangeRequest = requestData!!

                val params = passwordChangeRequest.toJson()

                val response = changePasswordUseCase(params = params)

                jobInProgress = false
                if (response.isFailed()) {
                    //
                    notifyJobComplete(
                        status = Status.FAILED,
                        message = response.message ?: "Something went wrong !"
                    )
                    return@launch
                }
                jobSuccess = true

                notifyJobComplete(
                    status = Status.SUCCESS,
                    message = "Password changed successfully !"
                )


            } catch (e: Exception) {
                e.printStackTrace()
                jobSuccess = false
                jobInProgress = false
                notifyJobComplete(
                    status = Status.FAILED,
                    message = "Your request failed for ${e.message}"
                )
            }

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


}