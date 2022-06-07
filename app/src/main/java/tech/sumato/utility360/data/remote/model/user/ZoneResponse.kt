package tech.sumato.utility360.data.remote.model.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ZoneResponse(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null
) : Parcelable