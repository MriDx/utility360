package tech.sumato.utility360.data.remote.model.meter

import android.os.Parcelable
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource
import kotlinx.parcelize.Parcelize
import tech.sumato.utility360.data.remote.model.utils.CreatedResource

@Parcelize
@Type("meter_installations")
data class MeterInstallationResource(
    val meter_installed_at: String = "",
    val meter_tested: Boolean = true,
    val after_installation_photo: String = "",
    val initial_meter_reading: String = "",
    val initial_meter_photo: String = "",
    val total_installation_fee: String = "",
    val tax_amount: String = "",
    val installation_item_description: String = "",
    val meter_serial_no: String = "",
    val created: CreatedResource? = null,
) : Parcelable, IzzyResource()