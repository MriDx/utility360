package tech.sumato.utility360.data.remote.model.utils

import androidx.annotation.Keep
import com.undabot.izzy.models.IzzyResource

@Keep
data class CreatedResource(
    val human: String? = null,
    val date: String? = null,
    val formatted: String? = null
)
