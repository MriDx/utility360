package tech.sumato.utility360.data.utils

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import tech.sumato.utility360.R

data class HomeFragmentActionData(
    @DrawableRes val icon: Int = 0,
    @StringRes val heading: Int = 0,
    val actionIdentifier: String = ""
)


fun getHomeFragmentActions(): List<HomeFragmentActionData> {
    return listOf(
        HomeFragmentActionData(
            icon = R.drawable.site_verification,
            heading = R.string.siteVerification,
            actionIdentifier = "site_verification"
        ),
        HomeFragmentActionData(
            icon = R.drawable.meter_installation,
            heading = R.string.meterInstallation,
            actionIdentifier = "meter_installation"
        ),
        HomeFragmentActionData(
            icon = R.drawable.pile_service,
            heading = R.string.pipelineService,
            actionIdentifier = "pipe_service"
        ),
       /* HomeFragmentActionData(
            icon = R.drawable.meter_installation,
            heading = R.string.meterQrAssociation,
            actionIdentifier = "meter_qr"
        ),*/
        HomeFragmentActionData(
            icon = R.drawable.meter_reading,
            heading = R.string.meterReading,
            actionIdentifier = "meter_reading"
        ),
        HomeFragmentActionData(
            icon = R.drawable.ic_baseline_repeat_24,
            heading = R.string.meterReplacement,
            actionIdentifier = "meter_replacement"
        ),
        HomeFragmentActionData(
            icon = R.drawable.site_service,
            heading = R.string.siteService,
            actionIdentifier = "site_service"
        ),


        )
}