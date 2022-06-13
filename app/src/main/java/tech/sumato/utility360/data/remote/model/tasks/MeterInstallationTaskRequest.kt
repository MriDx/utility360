package tech.sumato.utility360.data.remote.model.tasks

import androidx.annotation.Keep
import org.json.JSONObject

@Keep
data class MeterInstallationTaskRequest(
    var meter_serial_no: String = "",
    var initial_meter_reading: String = "",
    var total_installation_fee: String = "0.0",
    var tax_amount: String = "0.0",
    var installation_item_description: String = "",
    var initial_meter_photo: String = "",
    var after_installation_photo: String = "",
    var extraPipeLength: String = "",
    var uploadableMeterPhoto: String = "",
    var uploadableSitePhoto: String = "",
    var customerUuid: String = "",
    var qrData: String = "",
) {


    fun validate() =
        meter_serial_no.isNotEmpty() && uploadableMeterPhoto.isNotEmpty() && uploadableSitePhoto.isNotEmpty() && qrData.isNotEmpty()

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("meter_serial_no", meter_serial_no)
            put("initial_meter_reading", initial_meter_reading)
            put("total_installation_fee", total_installation_fee)
            put("tax_amount", tax_amount)
            put("installation_item_description", "Extra pipe involved - $extraPipeLength meter")
            put("initial_meter_photo", initial_meter_photo)
            put("after_installation_photo", after_installation_photo)
        }

    }

    fun isQrScanned() = qrData.isNotEmpty()

    fun getScannedQRJson() = JSONObject().apply {
        put("qr_code", qrData.split("/").last())
    }


}