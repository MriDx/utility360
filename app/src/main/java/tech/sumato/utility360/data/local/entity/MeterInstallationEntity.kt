package tech.sumato.utility360.data.local.entity

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.databinding.ObservableField
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import tech.sumato.utility360.data.local.entity.utils.JsonOperation
import tech.sumato.utility360.data.local.entity.utils.Validator
import java.util.concurrent.Flow

@Keep
@Parcelize
data class MeterInstallationEntity(
    var meterSerialNumber: String = "",
    var initialReading: Int = 0,
    var meterImageUrl: String = "",
    var siteImageUrl: String = "",
    var extraPipe: ObservableField<Float> = ObservableField(0f)
) : Parcelable, Validator, JsonOperation {


    override fun validate(): Boolean {
        return meterSerialNumber.isNotEmpty() && meterImageUrl.isNotEmpty() && siteImageUrl.isNotEmpty()
    }

    fun json(): JSONObject {
        return JSONObject().apply {
            put("serial_number", meterSerialNumber)
        }
    }

    override fun <T> toJson(): T {
        return JSONObject().apply {
            put("serial_number", meterSerialNumber)
            put("initial_reading", initialReading)
            put("meter_photo", meterImageUrl)
            put("site_photo", siteImageUrl)
            put("extra_pipe", extraPipe.get()!!.toInt())
        } as T
    }

    override fun <T, K> fromJson(source: T): K {
        return MeterInstallationEntity() as K
    }


}
