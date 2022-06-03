package tech.sumato.utility360.data.remote.model.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import tech.sumato.utility360.data.remote.model.utils.CreatedResource

@Keep
data class UserResponse(
    val type: String? = null,
    val id: String? = null,
    val attributes: UserAttributesResponse? = null,
)


@Keep
data class UserAttributesResponse(
    val name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val geographical_area: String? = null,
    val permissions: List<Any> = emptyList(),
    val created: CreatedResource? = null,
)