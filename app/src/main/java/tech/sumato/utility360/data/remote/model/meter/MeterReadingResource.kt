package tech.sumato.utility360.data.remote.model.meter

import android.os.Parcelable
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource
import kotlinx.parcelize.Parcelize
import tech.sumato.utility360.data.remote.model.utils.CreatedResource

@Parcelize
@Type("meter_readings")
data class MeterReadingResource(
    val meter_reading: String? = null,
    val date_of_billing: String? = null,
    val lat_long: String? = null,
    val meter_image: String? = null,
    val self_update: Boolean = false,
    val createdResource: CreatedResource? = null,
) : Parcelable, IzzyResource() {


}
