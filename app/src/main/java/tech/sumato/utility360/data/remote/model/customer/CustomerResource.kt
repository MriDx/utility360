package tech.sumato.utility360.data.remote.model.customer

import android.os.Parcelable
import androidx.annotation.Keep
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource
import kotlinx.parcelize.Parcelize
import tech.sumato.utility360.data.remote.model.grographical.GeographicalAreaResource
import tech.sumato.utility360.data.remote.model.site.SiteVerificationResource
import tech.sumato.utility360.data.remote.model.utils.CreatedResource

@Keep
@Type("customers")
@Parcelize
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
) : IzzyResource(), Parcelable {

    @Relationship("siteVerification")
    val siteVerification: SiteVerificationResource? = null

    @Relationship("geographicalArea")
    val geographicalArea: GeographicalAreaResource? = null


    fun getSecondaryDetailsMap(): Map<String, String> {
        return mapOf(
            "Connection type" to plan_type!!.replaceFirstChar { it.uppercase() },
            "Phone number" to contact!!,
            "Address" to address!!
        )
    }


}
