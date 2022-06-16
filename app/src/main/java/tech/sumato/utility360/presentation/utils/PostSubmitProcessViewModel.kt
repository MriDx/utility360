package tech.sumato.utility360.presentation.utils

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.receiveAsFlow
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.data.remote.utils.Status

open class PostSubmitProcessViewModel : ViewModel() {

    private var postSubmitProcessSendChannel: Channel<PostSubmitProcessData> =
        Channel<PostSubmitProcessData>()
    open val postSubmitProcessChannel = postSubmitProcessSendChannel.receiveAsFlow()


    val postSubmissionStatus = ObservableField(PostSubmitProcessData())

    open fun getSubmissionJob(): Job? {
        return null
    }

    fun isJobInProgress(): Boolean {
        return false
    }

    suspend fun notifyInProgress() {
        Log.d("mridx", "notifyInProgress: came to notify progress")
        postSubmitProcessSendChannel.send(
            PostSubmitProcessData(
                processStatus = ProcessStatus.LOADING,
                message = "Your request is being processed"
            )
        )
        postSubmissionStatus.set(
            postSubmissionStatus.get()?.copy(processStatus = ProcessStatus.LOADING)
        )
        postSubmissionStatus.notifyChange()
    }

    fun notifyJobComplete(status: Status) {
        notifyJobComplete(status = status, message = "")
    }

    fun notifyJobComplete(status: Status, message: String) {
        postSubmitProcessSendChannel.trySend(
            PostSubmitProcessData(
                processStatus = ProcessStatus.Completed(status = status),
                message = message
            )
        )
        postSubmissionStatus.set(
            postSubmissionStatus.get()
                ?.copy(processStatus = ProcessStatus.Completed(status = status), message = message)
        )
        postSubmissionStatus.notifyChange()
    }

    fun notifyJobCompletedWithException(
        status: Status,
        message: String,
        primaryBtn: String? = null
    ) {
        postSubmitProcessSendChannel.trySend(
            PostSubmitProcessData(
                processStatus = ProcessStatus.CompletedWithException(
                    status = status,
                    primaryBtn = primaryBtn
                ),
                message = message
            )
        )
        postSubmissionStatus.set(
            postSubmissionStatus.get()
                ?.copy(
                    processStatus = ProcessStatus.CompletedWithException(
                        status = status,
                        primaryBtn = primaryBtn
                    )
                )
        )
        postSubmissionStatus.notifyChange()
    }

    fun startJob() {
        getSubmissionJob()?.start()
    }


}