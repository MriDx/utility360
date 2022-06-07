package tech.sumato.utility360.data.remote.model.utils

import androidx.annotation.Keep

@Keep
data class ResponseResource<T>(
    val status: Int = 0,
    val message: String? = null,
    val data: T? = null
)
