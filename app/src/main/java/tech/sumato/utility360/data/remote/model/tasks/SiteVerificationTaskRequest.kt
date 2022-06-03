package tech.sumato.utility360.data.remote.model.tasks

import androidx.annotation.Keep
import org.json.JSONObject

@Keep
class SiteVerificationTaskRequest(
    var site_address_verified: Boolean = false,
    var site_feasibility: Boolean = false,
    var nearest_point: String = "",
    var remarks: String = "",
    var floor: String = "0",
    var photo: String = "",
    var customer_location: String = "",
    var domestic: Boolean = false,
    var commercial: Boolean = false
) {

    fun validate() =
        nearest_point.isNotEmpty() && photo.isNotEmpty() && customer_location.isNotEmpty()

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("site_address_verified", site_address_verified)
            put("site_feasibility", site_feasibility)
            put("nearest_point", nearest_point)
            put(
                "remarks",
                "${if (domestic) "Site is suitable for Domestic connection" else "Site is not suitable for Domestic connection"} \n ${if (commercial) "Site is suitable for Commercial connection" else "Site is not suitable for commercial connection"} \nRemarks - \n$remarks"
            )
            put("floor", floor.toIntOrNull() ?: 0)
            put("photo", photo)
            put("customer_location", customer_location)
        }
    }


}
