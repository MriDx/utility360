package tech.sumato.utility360.data.remote.model.user

import androidx.annotation.Keep
import org.json.JSONObject

@Keep
data class LoginResponse(
    val token_type: String? = null,
    val access_token: String? = null,
    val user: UserResponse? = null
) {

    fun getAccessToken() = "$token_type $access_token"

}


@Keep
data class LoginRequest(
    var email: String = "",
    var password: String = "",
    var device_name: String = ""
) {

    fun validate() = email.isNotEmpty() && password.isNotEmpty()

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("email", email)
            put("password", password)
            put("device_name", device_name)
        }
    }
}