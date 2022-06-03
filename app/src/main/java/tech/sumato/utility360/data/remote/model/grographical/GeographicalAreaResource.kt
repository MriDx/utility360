package tech.sumato.utility360.data.remote.model.grographical

import android.os.Parcelable
import androidx.annotation.Keep
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource
import kotlinx.parcelize.Parcelize
import tech.sumato.utility360.data.remote.model.utils.CreatedResource

@Keep
@Parcelize
@Type("geographical_areas")
data class GeographicalAreaResource(
    val name: String? = null,
    val ga_code: String? = null,
    val address: String? = null,
    val createdResource: CreatedResource? = null
) : Parcelable, IzzyResource()
