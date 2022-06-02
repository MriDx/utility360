package tech.sumato.utility360.data.remote.model.customer

import androidx.annotation.Keep
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.Resource
import tech.sumato.utility360.data.remote.model.site.SiteVerificationResource
import tech.sumato.utility360.data.remote.model.utils.CreatedResource

@Keep
@Type("customers")
data class CustomerResource(
    val name: String? = null,
    val email: String? = null,
    val pbg_id: String? = null,
    val contact: String? = null,
    val type: String? = null,
    val address: String? = null,
    val line_charge_date: String? = null,
    val plan_name: String? = null,
    val plan_type: String? = null,
    val photo: String? = null,
    val created: CreatedResource? = CreatedResource()
) : IzzyResource() {

    @Relationship("siteVerification")
    val siteVerification: SiteVerificationResource? = null


}
