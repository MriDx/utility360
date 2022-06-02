package tech.sumato.utility360.data.remote.model.user

import androidx.annotation.Keep

@Keep
data class LoginResponse(
    val token_type: String? = null,
    val access_token: String? = null,
)
