package com.mridx.apptemplate.data.remote.utils

data class Resource<out T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null
) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(status = Status.SUCCESS, data = data, null)
        }

        fun <T> error(message: String?): Resource<T> {
            return Resource(status = Status.FAILED, data = null, message = message)
        }

    }

}
