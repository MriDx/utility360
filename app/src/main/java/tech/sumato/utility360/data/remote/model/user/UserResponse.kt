package tech.sumato.utility360.data.remote.model.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UserResponse(
    @SerializedName("uuid") var uuid: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("role") var role: String? = null,
    @SerializedName("avatar") var avatar: String? = null,
    @SerializedName("designation") var designation: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("is_blocked") var isBlocked: Boolean? = null,
    @SerializedName("user_status") var userStatus: String? = null,
    @SerializedName("consumer_id") var consumerId: String? = null,
    @SerializedName("consumer_zone") var consumerZone: String? = null,
    @SerializedName("zones") var zones: ArrayList<ZoneResponse> = arrayListOf()
) : Parcelable
