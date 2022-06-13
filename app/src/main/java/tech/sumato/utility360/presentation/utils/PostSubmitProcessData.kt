package tech.sumato.utility360.presentation.utils

import tech.sumato.utility360.data.remote.utils.Status

sealed class ProcessStatus {
    object INITIAL : ProcessStatus()
    object LOADING : ProcessStatus()
    data class Completed(val status: Status) : ProcessStatus()
}

data class PostSubmitProcessData(
    val processStatus: ProcessStatus = ProcessStatus.INITIAL,
    val message: String = "",
) {

    fun isLoading(): Boolean = processStatus == ProcessStatus.LOADING

}