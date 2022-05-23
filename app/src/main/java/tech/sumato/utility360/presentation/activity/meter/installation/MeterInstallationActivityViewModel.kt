package tech.sumato.utility360.presentation.activity.meter.installation

import android.os.Bundle
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import tech.sumato.utility360.data.local.entity.MeterInstallationEntity
import tech.sumato.utility360.data.remote.utils.Status
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.presentation.utils.Navigation
import tech.sumato.utility360.presentation.utils.PostSubmitProcessViewModel
import javax.inject.Inject

@HiltViewModel
class MeterInstallationActivityViewModel @Inject constructor() : PostSubmitProcessViewModel(),
    Navigation {

    private var navigation_ = MutableSharedFlow<FragmentNavigation>()
    val navigation: SharedFlow<FragmentNavigation> = navigation_

    var meterInstallationEntityObserver: ObservableField<MeterInstallationEntity> =
        ObservableField(MeterInstallationEntity())


    private var pendingJob: Job? = null

    private var retries = 0

    fun submit() {
        pendingJob = viewModelScope.launch {
            try {
                val dataToPost = meterInstallationEntityObserver.get()?.toJson<JSONObject>()
                Log.d("mridx", "submit: $dataToPost")
                notifyInProgress()
                if (retries < 1) {
                    retries++
                    delay(1000 * 2)
                    cancel("for some reason")
                }
                delay(1000 * 3)
                notifyJobComplete(
                    status = Status.SUCCESS,
                    message = "Your request has been completed successfully "
                )
            } catch (e: Exception) {
                e.printStackTrace()
                notifyJobComplete(
                    status = Status.FAILED,
                    message = "Your request failed for ${e.message} "
                )
            }

        }
    }

    /**
     * starts pending job if any
     */
    fun startPendingJob() {
        pendingJob?.start()
    }


    override fun navigate(fragment: Class<*>) {
        viewModelScope.launch {
            navigation_.emit(
                FragmentNavigation(
                    fragment = fragment.newInstance() as Fragment
                )
            )
        }

    }

    override fun navigate(fragment: Class<*>, args: Bundle) {
        super.navigate(fragment, args)
    }

    fun readySubmitJob() {
        val obj = meterInstallationEntityObserver.get()?.toJson<JSONObject>()
        Log.d("mridx", "readySubmitJob: $obj")
        submit()
    }


}