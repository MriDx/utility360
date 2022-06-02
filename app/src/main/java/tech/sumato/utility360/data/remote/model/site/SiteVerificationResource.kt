package tech.sumato.utility360.data.remote.model.site

import androidx.annotation.Keep
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource
import tech.sumato.utility360.data.remote.model.utils.CreatedResource

@Keep
@Type("customer_checks")
data class SiteVerificationResource(
    val site_address_verified: String? = null,
    val site_feasibility: Boolean = false,
    val nearest_point: String? = null,
    val floor: Int = 0,
    val remarks: String? = null,
    val photograph_of_site: String? = null,
    val location: String? = null,
    val created: CreatedResource? = null,
) : IzzyResource()
