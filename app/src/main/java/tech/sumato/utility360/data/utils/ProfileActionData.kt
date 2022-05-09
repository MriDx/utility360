package tech.sumato.utility360.data.utils

import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import tech.sumato.utility360.R

data class ProfileActionData(
    @DrawableRes val icon: Int,
    val heading: String = "",
    val actionIdentifier: String = ""
)


fun getProfileActions() : List<ProfileActionData> {
    return listOf(
        ProfileActionData(
            icon = R.drawable.ic_settings,
            heading = "Settings",
            actionIdentifier = "settings"
        ),
        ProfileActionData(
            icon = R.drawable.ic_baseline_help_24,
            heading = "Support",
            actionIdentifier = "support"
        ),
        ProfileActionData(
            icon = R.drawable.ic_baseline_logout_24,
            heading = "Logout",
            actionIdentifier = "logout"
        ),
    )
}