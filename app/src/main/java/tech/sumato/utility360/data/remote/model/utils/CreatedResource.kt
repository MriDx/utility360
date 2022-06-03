package tech.sumato.utility360.data.remote.model.utils

import android.os.Parcelable
import androidx.annotation.Keep
import com.undabot.izzy.models.IzzyResource
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class CreatedResource(
    val human: String? = null,
    val date: String? = null,
    val formatted: String? = null
) : Parcelable
