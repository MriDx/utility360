package tech.sumato.utility360.data.remote.model.user

import androidx.annotation.Keep
import org.json.JSONObject

interface SettingsRequest {
    fun toJson(): JSONObject
}

@Keep
data class ChangePasswordRequest(
    var currentPassword: String = "",
    var newPassword: String = "",
    var confirmNewPassword: String = ""
) : SettingsRequest {

    var errors: MutableMap<String, String?> = mutableMapOf()

    fun validate(): Boolean {

        errors.clear()

        if (currentPassword.isEmpty()) {
            errors["current_password"] = "Current password can not be blank !"
        }
        if (newPassword.length < 8) {
            errors["new_password"] = "The password must be at least 8 characters !"
        }
        if (confirmNewPassword.isEmpty() || !confirmNewPassword.contentEquals(newPassword, false)) {
            errors["confirm_new_password"] = "Passwords do not match !"
        }

        /*if (errors.isEmpty()) {
            errors["current_password"] = null
            errors["new_password"] = null
            errors["confirm_new_password"] = null
        } */

        return errors.isEmpty()

    }


    /*currentPassword.isNotEmpty() && newPassword.isNotEmpty() && newPassword.contentEquals(
        confirmNewPassword,
        false
    )*/


    override fun toJson() = JSONObject().apply {
        put("current_password", currentPassword)
        put("password", newPassword)
        put("password_confirmation", confirmNewPassword)
    }


}
