package tech.sumato.utility360.data.remote.model.user

import android.os.Parcelable
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import tech.sumato.utility360.data.remote.model.meter.MeterReadingResource
import tech.sumato.utility360.data.remote.model.utils.CreatedResource

@Parcelize
@Type("users")
data class UserResource(
    val name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val geographical_area: Int = 0,
    val permissions: List<String> = emptyList(),
    val photo: String? = null,
    val created: CreatedResource? = null,
) : Parcelable, IzzyResource() {


    @Relationship("lastMeterReading")
    val lastMeterReading: MeterReadingResource? = null

}
