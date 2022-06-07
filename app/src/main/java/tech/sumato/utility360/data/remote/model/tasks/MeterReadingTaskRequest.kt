package tech.sumato.utility360.data.remote.model.tasks

import androidx.annotation.Keep
import org.json.JSONObject

@Keep
data class MeterReadingTaskRequest(
    var meter_readings: String = "0.0",
    var date_of_billing: String = "",
    var meter_image: String = "",
    var lat_long: String = "",
    var self_update: Boolean = false,
    var uploadableImagePath: String = "",
    var customerUuid: String = ""
) {

    fun validate() = uploadableImagePath.isNotEmpty()

    fun self() = MeterReadingTaskRequest(self_update = true)

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("meter_readings", meter_readings)
            put("date_of_billing", date_of_billing)
            put("meter_image", meter_image)
            put("lat_long", lat_long)
            put("self_update", self_update)
        }
    }

}
