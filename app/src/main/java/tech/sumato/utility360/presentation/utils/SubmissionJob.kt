package tech.sumato.utility360.presentation.utils

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import tech.sumato.utility360.data.remote.utils.Status

interface PostSubmitProcess {


    fun getSubmissionJob(): Job?

    fun isJobInProgress(): Boolean

    fun notifyInProgress()

    fun notifyJobComplete(status: Status)

    fun notifyJobComplete(status: Status, message: String = "")

    fun startJob()


}