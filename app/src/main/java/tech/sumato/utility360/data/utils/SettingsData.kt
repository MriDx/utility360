package tech.sumato.utility360.data.utils

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import dagger.Provides
import tech.sumato.utility360.R


@Keep
data class SettingsItemData(
    @StringRes val title: Int = 0,
    @StringRes val subTitle: Int? = null,
    @DrawableRes val icon: Int = 0,
    val actionIdentifier: String = ""
)


fun settingsItems() = listOf(
    SettingsItemData(
        title = R.string.accountSettings,
        icon = R.drawable.ic_outline_account_circle_24,
        actionIdentifier = "account_settings"
    ),
    SettingsItemData(
        title = R.string.generalSettings,
        icon = R.drawable.ic_outline_settings_24,
        actionIdentifier = "general_settings"
    ),
    SettingsItemData(
        title = R.string.notificationSettings,
        icon = R.drawable.ic_outline_notifications_24,
        actionIdentifier = "notification_settings"
    )
)