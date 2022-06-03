package tech.sumato.utility360.data.remote.model.user

import androidx.annotation.Keep

@Keep
data class LogoutResponse(
    val data: Int = 0,
    val message: String? = null
)
